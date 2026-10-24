import java.util.*;

public class BankTest {
    static int passed = 0, failed = 0;

    static void check(String name, Object expected, Object actual) {
        if (Objects.equals(expected, actual)) { passed++; }
        else { failed++; System.out.println("  FAIL " + name + "\n       expected: " + expected + "\n       actual:   " + actual); }
    }

    interface Level { void run(); }

    static void level(int n, Level body) {
        System.out.println("Level " + n);
        int before = failed;
        try { body.run(); }
        catch (UnsupportedOperationException e) { failed++; System.out.println("  not implemented yet"); }
        catch (Exception e) { failed++; System.out.println("  ERROR " + e); e.printStackTrace(System.out); }
        System.out.println(failed == before ? "  ok" : "  has failures");
    }

    public static void main(String[] args) {
        int max = args.length > 0 ? Integer.parseInt(args[0]) : 4;

        if (max >= 1) level(1, () -> {
            Bank b = new Bank();
            check("create", true, b.createAccount(1, "A"));
            check("create duplicate", false, b.createAccount(2, "A"));
            check("create second", true, b.createAccount(3, "B"));
            check("deposit", 100, b.deposit(4, "A", 100));
            check("deposit adds up", 150, b.deposit(5, "A", 50));
            check("deposit missing", null, b.deposit(6, "Z", 10));
            check("transfer", 120, b.transfer(7, "A", "B", 30));
            check("target credited", 30, b.getBalance(8, "B"));
            check("insufficient funds", null, b.transfer(9, "B", "A", 31));
            check("exact balance ok", 0, b.transfer(10, "B", "A", 30));
            check("same account", null, b.transfer(11, "A", "A", 1));
            check("missing target", null, b.transfer(12, "A", "Z", 1));
            check("missing source", null, b.transfer(13, "Z", "A", 1));
            check("failed transfers change nothing", 150, b.getBalance(14, "A"));
            check("balance missing", null, b.getBalance(15, "Z"));
        });

        if (max >= 2) level(2, () -> {
            Bank b = new Bank();
            b.createAccount(1, "A"); b.createAccount(2, "B"); b.createAccount(3, "C"); b.createAccount(4, "D");
            b.deposit(5, "A", 100); b.deposit(6, "B", 100); b.deposit(7, "C", 100);
            b.transfer(8, "A", "B", 40);
            b.transfer(9, "C", "A", 40);
            b.transfer(10, "B", "D", 10);
            b.transfer(11, "B", "D", 500);  // fails, must not count
            check("ties by id, zero included", List.of("A(40)", "C(40)", "B(10)", "D(0)"), b.topSpenders(12, 10));
            check("top n", List.of("A(40)", "C(40)"), b.topSpenders(13, 2));
        });

        if (max >= 3) level(3, () -> {
            Bank b = new Bank();
            b.createAccount(1, "A"); b.createAccount(2, "B");
            b.deposit(3, "A", 100);
            check("schedule first", "payment1", b.schedulePayment(4, "A", 30, 10));  // runs at 14
            check("schedule second", "payment2", b.schedulePayment(5, "A", 80, 5));  // runs at 10
            check("schedule third", "payment3", b.schedulePayment(6, "B", 10, 1));   // runs at 7, B has 0 -> skipped
            check("schedule missing account", null, b.schedulePayment(7, "Z", 1, 1));
            check("not yet due", 100, b.getBalance(9, "A"));
            check("due exactly at ts", 20, b.getBalance(10, "A"));
            check("skipped when insufficient", 20, b.getBalance(15, "A"));
            check("skipped payment left B alone", 0, b.getBalance(16, "B"));
            check("executed payments count as spend", List.of("A(80)", "B(0)"), b.topSpenders(17, 5));
            check("counter skips failed schedule", "payment4", b.schedulePayment(18, "A", 5, 10)); // runs at 28
            check("cancel wrong account", false, b.cancelPayment(19, "B", "payment4"));
            check("cancel", true, b.cancelPayment(20, "A", "payment4"));
            check("cancel twice", false, b.cancelPayment(21, "A", "payment4"));
            check("cancel executed", false, b.cancelPayment(22, "A", "payment2"));
            check("cancelled payment never runs", 20, b.getBalance(30, "A"));
            b.deposit(31, "B", 50);
            b.schedulePayment(32, "B", 30, 8);  // payment5 at 40
            b.schedulePayment(33, "B", 30, 7);  // payment6 at 40
            check("same time -> creation order", 20, b.getBalance(40, "B"));
        });

        if (max >= 4) level(4, () -> {
            Bank b = new Bank();
            b.createAccount(1, "A"); b.createAccount(2, "B"); b.createAccount(3, "C");
            b.deposit(4, "A", 100);
            b.deposit(5, "B", 50);
            b.transfer(6, "B", "C", 10);
            b.schedulePayment(7, "B", 25, 10);  // payment1 at 17
            b.schedulePayment(8, "B", 5, 20);   // payment2 at 28
            check("merge", true, b.mergeAccounts(9, "A", "B"));
            check("balances combined", 140, b.getBalance(10, "A"));
            check("source deleted", null, b.getBalance(11, "B"));
            check("source unusable", null, b.transfer(12, "B", "C", 1));
            check("spend combined, source gone", List.of("A(10)", "C(0)"), b.topSpenders(13, 5));
            check("merge self", false, b.mergeAccounts(14, "A", "A"));
            check("merge missing", false, b.mergeAccounts(15, "A", "Z"));
            check("moved payment runs from target", 115, b.getBalance(17, "A"));
            check("moved payment cancellable by target", true, b.cancelPayment(18, "A", "payment2"));
            check("spend includes moved payment", List.of("A(35)"), b.topSpenders(19, 1));
            check("id reusable", true, b.createAccount(20, "B"));
            check("cancelled stays cancelled", 115, b.getBalance(30, "A"));
        });

        System.out.println("\n" + passed + " passed, " + failed + " failed");
        if (failed > 0) System.exit(1);
    }
}
