import java.util.*;

public class JobMatcher {

    // ---- Level 1 ----
    public boolean addJob(String jobId, String title, List<String> skills, String location, int salary) { throw new UnsupportedOperationException(); }
    public boolean addSeeker(String seekerId, List<String> skills, String location, int minSalary) { throw new UnsupportedOperationException(); }

    // ---- Level 2 ----
    public List<String> match(String seekerId, int k) { throw new UnsupportedOperationException(); }

    // ---- Level 3 ----
    public boolean apply(String seekerId, String jobId) { throw new UnsupportedOperationException(); }
    public boolean closeJob(String jobId) { throw new UnsupportedOperationException(); }
}
