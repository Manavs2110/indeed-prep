import java.util.*;

/**
 * Reference solution.
 * Pending payments live in a min-heap ordered by (executeAt, sequence); every public method
 * first calls processDue(ts). Cancellation is lazy (flag + skip when polled).
 * Merge re-points pending payments by walking the pending map: O(P). Discuss: an
 * index accountId -> pending payments would make merge O(payments of source).
 */
public class Bank {

    private static final class Account {
        int balance;
        long outgoing;
    }

    private static final class Payment {
        final String id;
        String accountId;
        final int amount;
        final long executeAt;
        final long seq;

        Payment(String id, String accountId, int amount, long executeAt, long seq) {
            this.id = id; this.accountId = accountId; this.amount = amount; this.executeAt = executeAt; this.seq = seq;
        }
    }

    private final Map<String, Account> accounts = new HashMap<>();
    private final Map<String, Payment> pending = new HashMap<>();
    private final PriorityQueue<Payment> queue = new PriorityQueue<>(
            Comparator.comparingLong((Payment p) -> p.executeAt).thenComparingLong(p -> p.seq));
    private long paymentCounter = 0;

    // ---- Level 1 ----
    public boolean createAccount(long ts, String accountId) {
        processDue(ts);
        return accounts.putIfAbsent(accountId, new Account()) == null;
    }

    public Integer deposit(long ts, String accountId, int amount) {
        processDue(ts);
        Account a = accounts.get(accountId);
        if (a == null) return null;
        a.balance += amount;
        return a.balance;
    }

    public Integer transfer(long ts, String sourceId, String targetId, int amount) {
        processDue(ts);
        Account src = accounts.get(sourceId), dst = accounts.get(targetId);
        if (src == null || dst == null || sourceId.equals(targetId) || src.balance < amount) return null;
        src.balance -= amount;
        src.outgoing += amount;
        dst.balance += amount;
        return src.balance;
    }

    public Integer getBalance(long ts, String accountId) {
        processDue(ts);
        Account a = accounts.get(accountId);
        return a == null ? null : a.balance;
    }

    // ---- Level 2 ----
    public List<String> topSpenders(long ts, int n) {
        processDue(ts);
        List<Map.Entry<String, Account>> all = new ArrayList<>(accounts.entrySet());
        all.sort(Comparator.<Map.Entry<String, Account>>comparingLong(e -> -e.getValue().outgoing)
                .thenComparing(Map.Entry::getKey));
        List<String> out = new ArrayList<>();
        for (int i = 0; i < Math.min(n, all.size()); i++) {
            out.add(all.get(i).getKey() + "(" + all.get(i).getValue().outgoing + ")");
        }
        return out;
    }

    // ---- Level 3 ----
    public String schedulePayment(long ts, String accountId, int amount, long delay) {
        processDue(ts);
        if (!accounts.containsKey(accountId)) return null;
        paymentCounter++;
        Payment p = new Payment("payment" + paymentCounter, accountId, amount, ts + delay, paymentCounter);
        pending.put(p.id, p);
        queue.offer(p);
        return p.id;
    }

    public boolean cancelPayment(long ts, String accountId, String paymentId) {
        processDue(ts);
        Payment p = pending.get(paymentId);
        if (p == null || !p.accountId.equals(accountId)) return false;
        pending.remove(paymentId); // the queue entry is skipped when polled
        return true;
    }

    // ---- Level 4 ----
    public boolean mergeAccounts(long ts, String targetId, String sourceId) {
        processDue(ts);
        if (targetId.equals(sourceId)) return false;
        Account target = accounts.get(targetId), source = accounts.get(sourceId);
        if (target == null || source == null) return false;
        target.balance += source.balance;
        target.outgoing += source.outgoing;
        for (Payment p : pending.values()) {
            if (p.accountId.equals(sourceId)) p.accountId = targetId;
        }
        accounts.remove(sourceId);
        return true;
    }

    private void processDue(long ts) {
        while (!queue.isEmpty() && queue.peek().executeAt <= ts) {
            Payment p = queue.poll();
            if (pending.remove(p.id) == null) continue; // cancelled
            Account a = accounts.get(p.accountId);
            if (a == null || a.balance < p.amount) continue; // skipped
            a.balance -= p.amount;
            a.outgoing += p.amount;
        }
    }
}
