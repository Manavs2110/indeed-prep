import java.time.Instant;
import java.util.*;

/** Regression tests, one per ticket. Do not edit — fix the code. */
public class JobBoardTest {
    static int passed = 0, failed = 0;

    static void check(String name, Object expected, Object actual) {
        if (Objects.equals(expected, actual)) { passed++; System.out.println("  pass " + name); }
        else { failed++; System.out.println("  FAIL " + name + "\n       expected: " + expected + "\n       actual:   " + actual); }
    }

    static List<String> ids(List<Job> jobs) {
        return jobs.stream().map(Job::id).toList();
    }

    public static void main(String[] args) {
        Instant now = Instant.parse("2026-10-25T12:00:00Z");

        System.out.println("BUG-101 search is case-insensitive");
        check("'Java' in Austin", List.of("j1", "j7"), ids(new SearchService(new JobRepository()).search("Java", "Austin", 1, 10)));

        System.out.println("BUG-102 page 1 shows the newest results");
        SearchService s2 = new SearchService(new JobRepository());
        check("page 1", List.of("j1", "j2"), ids(s2.search("engineer", "Austin", 1, 2)));
        check("page 2", List.of("j6"), ids(s2.search("engineer", "Austin", 2, 2)));

        System.out.println("BUG-103 one search doesn't affect the next");
        SearchService s3 = new SearchService(new JobRepository());
        check("first search", List.of("j3"), ids(s3.search("analyst", "Remote", 1, 10)));
        check("second search", List.of("j5"), ids(s3.search("engineer", "Seattle", 1, 10)));

        System.out.println("BUG-104 applying to open jobs works");
        ApplicationService apps = new ApplicationService(new JobRepository());
        check("open job", "APPLIED", apps.apply("s1", "j1"));
        check("twice", "ALREADY_APPLIED", apps.apply("s1", "j1"));
        check("closed job", "JOB_CLOSED", apps.apply("s1", "j4"));
        check("unknown job", "NOT_FOUND", apps.apply("s1", "nope"));

        System.out.println("BUG-105 median salary");
        SalaryInsights si = new SalaryInsights(new JobRepository());
        check("Austin", 120000, si.medianSalary("Austin"));
        check("Seattle", 170000, si.medianSalary("Seattle"));
        check("no jobs", 0, si.medianSalary("Nowhere"));

        System.out.println("BUG-106 NEW badge only within 24h");
        JobRepository r6 = new JobRepository();
        check("4h old", true, JobBadges.isNew(r6.findById("j1").get(), now));
        check("27h old", false, JobBadges.isNew(r6.findById("j2").get(), now));
        check("5 days old", false, JobBadges.isNew(r6.findById("j6").get(), now));

        System.out.println("\n" + passed + " passed, " + failed + " failed");
        if (failed > 0) System.exit(1);
    }
}
