import java.util.*;

public class SalaryInsights {

    private final JobRepository repo;

    public SalaryInsights(JobRepository repo) {
        this.repo = repo;
    }

    /** Median advertised salary for a location, across all postings. 0 if none. */
    public int medianSalary(String location) {
        List<Integer> salaries = new ArrayList<>();
        for (Job j : repo.findAll()) {
            if (j.location().equalsIgnoreCase(location)) salaries.add(j.salary());
        }
        if (salaries.isEmpty()) return 0;
        Collections.sort(salaries);                                     // BUG-105: median needs sorted data
        int mid = salaries.size() / 2;
        if (salaries.size() % 2 == 1) return salaries.get(mid);
        return (salaries.get(mid - 1) + salaries.get(mid)) / 2;
    }
}
