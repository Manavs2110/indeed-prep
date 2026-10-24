import java.util.*;

public class JobMatcherTest {
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

    static JobMatcher sample() {
        JobMatcher m = new JobMatcher();
        m.addJob("j1", "Backend Engineer", List.of("Java", "SQL", "AWS"), "Austin", 150);
        m.addJob("j2", "Full Stack", List.of("java", "react"), "Austin", 160);
        m.addJob("j3", "Data Engineer", List.of("SQL", "Python", "AWS"), "Remote", 140);
        m.addJob("j4", "Platform", List.of("Java", "AWS", "Kubernetes"), "Seattle", 200);
        m.addJob("j5", "Junior Dev", List.of("Java"), "austin", 90);
        m.addJob("j6", "Analyst", List.of("Excel"), "Austin", 150);
        m.addJob("j0", "Backend II", List.of("JAVA", "sql", "aws"), "Austin", 150);
        m.addSeeker("s1", List.of("Java", "SQL", "AWS", "java"), "Austin", 100);
        return m;
    }

    public static void main(String[] args) {
        int max = args.length > 0 ? Integer.parseInt(args[0]) : 4;

        if (max >= 1) level(1, () -> {
            JobMatcher m = new JobMatcher();
            check("add job", true, m.addJob("j1", "SWE", List.of("Java"), "Austin", 100));
            check("duplicate job", false, m.addJob("j1", "Other", List.of("Go"), "NYC", 999));
            check("add seeker", true, m.addSeeker("s1", List.of("Java"), "Austin", 50));
            check("duplicate seeker", false, m.addSeeker("s1", List.of("Go"), "NYC", 0));
            check("job and seeker ids are separate namespaces", true, m.addSeeker("j1", List.of("Java"), "Austin", 0));
        });

        if (max >= 2) level(2, () -> {
            JobMatcher m = sample();
            // overlap: j0=3, j1=3, j3=2 (Remote), j2=1, j5 excluded (salary 90 < 100), j4 excluded (Seattle), j6 no overlap
            check("ranking", List.of("j0", "j1", "j3", "j2"), m.match("s1", 10));
            check("top k", List.of("j0", "j1"), m.match("s1", 2));
            check("unknown seeker", List.of(), m.match("nobody", 5));
            m.addSeeker("s2", List.of("Cobol"), "Austin", 0);
            check("no overlap", List.of(), m.match("s2", 5));
            m.addSeeker("s3", List.of("java"), "AUSTIN", 0);
            // all overlap=1: j2(160) j0(150) j1(150) j5(90)
            check("tie-break salary then id, case-insensitive location", List.of("j2", "j0", "j1", "j5"), m.match("s3", 10));
            check("k = 0", List.of(), m.match("s3", 0));
        });

        if (max >= 3) level(3, () -> {
            JobMatcher m = sample();
            check("apply", true, m.apply("s1", "j0"));
            check("apply twice", false, m.apply("s1", "j0"));
            check("apply unknown job", false, m.apply("s1", "nope"));
            check("apply unknown seeker", false, m.apply("nope", "j1"));
            check("applied job excluded", List.of("j1", "j3", "j2"), m.match("s1", 10));
            check("close job", true, m.closeJob("j1"));
            check("close twice", false, m.closeJob("j1"));
            check("close unknown", false, m.closeJob("nope"));
            check("closed job excluded", List.of("j3", "j2"), m.match("s1", 10));
            check("cannot apply to closed job", false, m.apply("s1", "j1"));
        });

        if (max >= 4) level(4, () -> {
            Random rnd = new Random(42);
            String[] cities = new String[21];
            for (int i = 0; i < 20; i++) cities[i] = "City" + i;
            cities[20] = "Remote";
            long start = System.nanoTime();
            JobMatcher m = new JobMatcher();
            for (int i = 0; i < 200_000; i++) {
                List<String> skills = new ArrayList<>();
                for (int s = 0; s < 5; s++) skills.add("skill" + rnd.nextInt(500));
                m.addJob("job" + i, "t", skills, cities[rnd.nextInt(21)], 50 + rnd.nextInt(150));
            }
            for (int i = 0; i < 2_000; i++) {
                List<String> skills = new ArrayList<>();
                for (int s = 0; s < 5; s++) skills.add("skill" + rnd.nextInt(500));
                m.addSeeker("seeker" + i, skills, cities[rnd.nextInt(20)], 50 + rnd.nextInt(100));
            }
            int nonEmpty = 0;
            for (int i = 0; i < 2_000; i++) if (!m.match("seeker" + i, 10).isEmpty()) nonEmpty++;
            double secs = (System.nanoTime() - start) / 1e9;
            System.out.printf("  %.2fs total, %d seekers got matches%n", secs, nonEmpty);
            check("most seekers get matches", true, nonEmpty > 1_500);
            check("finishes under 3s", true, secs < 3.0);
        });

        System.out.println("\n" + passed + " passed, " + failed + " failed");
        if (failed > 0) System.exit(1);
    }
}
