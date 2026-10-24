import java.util.*;

public class InMemoryDbTest {
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
            InMemoryDb db = new InMemoryDb();
            check("get missing record", null, db.get("A", "x"));
            db.set("A", "x", "1");
            check("get after set", "1", db.get("A", "x"));
            check("get missing field", null, db.get("A", "y"));
            db.set("A", "x", "2");
            check("overwrite", "2", db.get("A", "x"));
            db.set("B", "x", "9");
            check("records are independent", "2", db.get("A", "x"));
            check("delete existing", true, db.delete("A", "x"));
            check("delete again", false, db.delete("A", "x"));
            check("get after delete", null, db.get("A", "x"));
            check("delete missing record", false, db.delete("Z", "x"));
        });

        if (max >= 2) level(2, () -> {
            InMemoryDb db = new InMemoryDb();
            db.set("A", "b", "2");
            db.set("A", "a", "1");
            db.set("A", "ab", "3");
            db.set("A", "c", "4");
            check("scan sorted", List.of("a(1)", "ab(3)", "b(2)", "c(4)"), db.scan("A"));
            check("scan missing record", List.of(), db.scan("Q"));
            check("scanByPrefix", List.of("a(1)", "ab(3)"), db.scanByPrefix("A", "a"));
            check("scanByPrefix none", List.of(), db.scanByPrefix("A", "z"));
            db.delete("A", "a");
            check("scan after delete", List.of("ab(3)", "b(2)", "c(4)"), db.scan("A"));
            db.delete("A", "ab"); db.delete("A", "b"); db.delete("A", "c");
            check("empty record disappears", List.of(), db.scan("A"));
        });

        if (max >= 3) level(3, () -> {
            InMemoryDb db = new InMemoryDb();
            db.setAtWithTtl("A", "x", "1", 10, 5);   // visible [10, 15)
            db.setAt("A", "y", "2", 11);
            check("ttl visible", "1", db.getAt("A", "x", 14));
            check("ttl expired at boundary", null, db.getAt("A", "x", 15));
            check("non-ttl visible", "2", db.getAt("A", "y", 16));
            check("scan excludes expired", List.of("y(2)"), db.scanAt("A", 17));
            check("delete expired", false, db.deleteAt("A", "x", 18));
            db.setAtWithTtl("A", "z", "3", 20, 10);  // visible [20, 30)
            db.setAt("A", "z", "4", 21);             // overwrite removes TTL
            check("overwrite clears ttl", "4", db.getAt("A", "z", 100));
            db.setAtWithTtl("B", "pa", "1", 30, 5);
            db.setAtWithTtl("B", "pb", "2", 31, 100);
            check("prefix scan with ttl", List.of("pa(1)", "pb(2)"), db.scanByPrefixAt("B", "p", 34));
            check("prefix scan after expiry", List.of("pb(2)"), db.scanByPrefixAt("B", "p", 35));
            check("delete live ttl field", true, db.deleteAt("B", "pb", 36));
            check("record gone", List.of(), db.scanAt("B", 37));
        });

        if (max >= 4) level(4, () -> {
            InMemoryDb db = new InMemoryDb();
            db.setAtWithTtl("A", "x", "1", 1, 10);   // expires at 11
            db.setAt("B", "y", "2", 2);
            db.setAtWithTtl("C", "z", "3", 3, 2);    // expires at 5
            check("backup counts live records", 2, db.backup(5));
            check("delete after backup", true, db.deleteAt("B", "y", 6));
            db.setAt("D", "w", "4", 7);
            check("backup after changes", 2, db.backup(8)); // A and D
            db.restore(20, 7);                       // -> backup @5; A.x had 6 left
            check("restored non-ttl field", "2", db.getAt("B", "y", 21));
            check("field added after backup is gone", null, db.getAt("D", "w", 22));
            check("restored ttl still live", "1", db.getAt("A", "x", 25));
            check("restored ttl expires at ts+remaining", null, db.getAt("A", "x", 26));
            check("expired-at-backup field not restored", null, db.getAt("C", "z", 27));
            db.setAt("B", "y", "changed", 28);
            db.restore(30, 6);                       // backup @5 again, must be unaffected
            check("backup immune to later mutation", "2", db.getAt("B", "y", 31));
            db.restore(40, 100);                     // latest backup = @8
            check("latest backup chosen", "4", db.getAt("D", "w", 41));
            check("latest backup excludes deleted", null, db.getAt("B", "y", 42));
        });

        System.out.println("\n" + passed + " passed, " + failed + " failed");
        if (failed > 0) System.exit(1);
    }
}
