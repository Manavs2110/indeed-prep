import java.util.*;

public class Bank {

    // ---- Level 1 ----
    public boolean createAccount(long ts, String accountId) { throw new UnsupportedOperationException(); }
    public Integer deposit(long ts, String accountId, int amount) { throw new UnsupportedOperationException(); }
    public Integer transfer(long ts, String sourceId, String targetId, int amount) { throw new UnsupportedOperationException(); }
    public Integer getBalance(long ts, String accountId) { throw new UnsupportedOperationException(); }

    // ---- Level 2 ----
    public List<String> topSpenders(long ts, int n) { throw new UnsupportedOperationException(); }

    // ---- Level 3 ----
    public String schedulePayment(long ts, String accountId, int amount, long delay) { throw new UnsupportedOperationException(); }
    public boolean cancelPayment(long ts, String accountId, String paymentId) { throw new UnsupportedOperationException(); }

    // ---- Level 4 ----
    public boolean mergeAccounts(long ts, String targetId, String sourceId) { throw new UnsupportedOperationException(); }
}
