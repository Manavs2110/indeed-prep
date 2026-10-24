import java.time.Instant;

/** Prints what a user would see. Use it to reproduce the tickets. */
public class Main {
    public static void main(String[] args) {
        JobRepository repo = new JobRepository();
        SearchService search = new SearchService(repo);
        ApplicationService apps = new ApplicationService(repo);
        SalaryInsights salary = new SalaryInsights(repo);
        Instant now = Instant.parse("2026-10-25T12:00:00Z");

        System.out.println("== Homepage (badges) ==");
        for (Job j : repo.findAll()) {
            System.out.printf("  %-26s %-8s %s%n", j.title(), j.location(), JobBadges.isNew(j, now) ? "[NEW]" : "");
        }

        System.out.println("\n== Salary insights ==");
        System.out.println("  Austin median: $" + salary.medianSalary("Austin"));

        System.out.println("\n== Apply ==");
        System.out.println("  s1 -> j1: " + apps.apply("s1", "j1"));

        System.out.println("\n== Search 'Java' in Austin, page 1 ==");
        search.search("Java", "Austin", 1, 10).forEach(j -> System.out.println("  " + j.id() + " " + j.title()));

        System.out.println("\n== Search 'engineer' in Austin, page 1 (size 2) ==");
        search.search("engineer", "Austin", 1, 2).forEach(j -> System.out.println("  " + j.id() + " " + j.title()));

        System.out.println("\n== Search 'engineer' in Seattle, page 1 ==");
        search.search("engineer", "Seattle", 1, 10).forEach(j -> System.out.println("  " + j.id() + " " + j.title()));
    }
}
