import java.util.*;

/**
 * Reference solution.
 * Key idea (Level 4): an inverted index skill -> jobs. match() only scores jobs that share
 * at least one skill with the seeker, instead of scanning all J jobs.
 *
 * addJob: O(s). match: O(C + C log k) where C = sum of posting-list sizes for the seeker's skills.
 * Further ideas to discuss: index by (location, skill) to shrink C; keep posting lists sorted by
 * salary for early exit; remove closed jobs from postings (lazy vs eager deletion tradeoff).
 */
public class JobMatcher {

    private record Job(String id, Set<String> skills, String location, int salary) {}
    private record Seeker(String id, Set<String> skills, String location, int minSalary) {}

    private static final Comparator<Map.Entry<Job, Integer>> RANK =
            Comparator.<Map.Entry<Job, Integer>>comparingInt(e -> -e.getValue())
                    .thenComparingInt(e -> -e.getKey().salary())
                    .thenComparing(e -> e.getKey().id());

    private final Map<String, Job> jobs = new HashMap<>();
    private final Map<String, Seeker> seekers = new HashMap<>();
    private final Map<String, List<Job>> jobsBySkill = new HashMap<>();
    private final Set<String> closed = new HashSet<>();
    private final Map<String, Set<String>> appliedBySeeker = new HashMap<>();

    // ---- Level 1 ----
    public boolean addJob(String jobId, String title, List<String> skills, String location, int salary) {
        if (jobs.containsKey(jobId)) return false;
        Job job = new Job(jobId, normalize(skills), location.toLowerCase(), salary);
        jobs.put(jobId, job);
        for (String s : job.skills()) jobsBySkill.computeIfAbsent(s, k -> new ArrayList<>()).add(job);
        return true;
    }

    public boolean addSeeker(String seekerId, List<String> skills, String location, int minSalary) {
        if (seekers.containsKey(seekerId)) return false;
        seekers.put(seekerId, new Seeker(seekerId, normalize(skills), location.toLowerCase(), minSalary));
        return true;
    }

    // ---- Level 2 ----
    public List<String> match(String seekerId, int k) {
        Seeker seeker = seekers.get(seekerId);
        if (seeker == null || k <= 0) return List.of();
        Set<String> applied = appliedBySeeker.getOrDefault(seekerId, Set.of());

        Map<Job, Integer> overlap = new HashMap<>();
        for (String skill : seeker.skills()) {
            for (Job job : jobsBySkill.getOrDefault(skill, List.of())) {
                if (job.salary() < seeker.minSalary()) continue;
                if (!job.location().equals(seeker.location()) && !job.location().equals("remote")) continue;
                if (closed.contains(job.id()) || applied.contains(job.id())) continue;
                overlap.merge(job, 1, Integer::sum);
            }
        }

        // max-heap of size k on the reversed comparator = keep the k best
        PriorityQueue<Map.Entry<Job, Integer>> heap = new PriorityQueue<>(RANK.reversed());
        for (Map.Entry<Job, Integer> e : overlap.entrySet()) {
            heap.offer(e);
            if (heap.size() > k) heap.poll();
        }
        List<Map.Entry<Job, Integer>> best = new ArrayList<>(heap);
        best.sort(RANK);
        List<String> out = new ArrayList<>(best.size());
        for (Map.Entry<Job, Integer> e : best) out.add(e.getKey().id());
        return out;
    }

    // ---- Level 3 ----
    public boolean apply(String seekerId, String jobId) {
        if (!seekers.containsKey(seekerId) || !jobs.containsKey(jobId) || closed.contains(jobId)) return false;
        return appliedBySeeker.computeIfAbsent(seekerId, k -> new HashSet<>()).add(jobId);
    }

    public boolean closeJob(String jobId) {
        return jobs.containsKey(jobId) && closed.add(jobId);
    }

    private static Set<String> normalize(List<String> skills) {
        Set<String> out = new HashSet<>();
        for (String s : skills) out.add(s.toLowerCase());
        return out;
    }
}
