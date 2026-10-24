import java.util.*;

public class SearchService {

    private final JobRepository repo;

    public SearchService(JobRepository repo) {
        this.repo = repo;
    }

    /**
     * Keyword + location search over open jobs, newest first.
     * @param page 1-based page number, as sent by the web client
     */
    public List<Job> search(String query, String location, int page, int pageSize) {
        List<Job> results = repo.findAll();
        results.removeIf(j -> !j.title().toLowerCase().contains(query));

        List<Job> filtered = results.stream()
                .filter(j -> j.location().equalsIgnoreCase(location))
                .filter(j -> j.status().equals("OPEN"))
                .sorted(Comparator.comparing(Job::postedAt).reversed())
                .toList();

        int from = page * pageSize;
        if (from >= filtered.size()) return List.of();
        return filtered.subList(from, Math.min(from + pageSize, filtered.size()));
    }
}
