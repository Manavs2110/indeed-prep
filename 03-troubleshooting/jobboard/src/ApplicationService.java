import java.util.*;

public class ApplicationService {

    private final JobRepository repo;
    private final Map<String, Set<String>> appliedBySeeker = new HashMap<>();

    public ApplicationService(JobRepository repo) {
        this.repo = repo;
    }

    /** @return APPLIED, ALREADY_APPLIED, JOB_CLOSED or NOT_FOUND */
    public String apply(String seekerId, String jobId) {
        Optional<Job> job = repo.findById(jobId);
        if (job.isEmpty()) return "NOT_FOUND";
        if (job.get().status() != "OPEN") return "JOB_CLOSED";
        if (!appliedBySeeker.computeIfAbsent(seekerId, k -> new HashSet<>()).add(jobId)) {
            return "ALREADY_APPLIED";
        }
        return "APPLIED";
    }
}
