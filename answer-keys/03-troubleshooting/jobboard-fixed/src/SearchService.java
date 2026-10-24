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
        if (page < 1 || pageSize < 1) throw new IllegalArgumentException("page and pageSize must be >= 1");
        String q = query.toLowerCase(Locale.ROOT);                      // BUG-101

        List<Job> filtered = repo.findAll().stream()                    // BUG-103: filter a stream, don't removeIf the shared list
                .filter(j -> j.title().toLowerCase(Locale.ROOT).contains(q))
                .filter(j -> j.location().equalsIgnoreCase(location))
                .filter(j -> j.status().equals("OPEN"))
                .sorted(Comparator.comparing(Job::postedAt).reversed())
                .toList();

        int from = (page - 1) * pageSize;                               // BUG-102: page is 1-based
        if (from >= filtered.size()) return List.of();
        return filtered.subList(from, Math.min(from + pageSize, filtered.size()));
    }
}
