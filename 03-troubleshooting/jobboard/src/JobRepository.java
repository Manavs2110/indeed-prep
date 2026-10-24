import java.time.Instant;
import java.util.*;

/** In-memory stand-in for the jobs table. Seeded from an export of production data. */
public class JobRepository {

    private static final String SEED = """
            j1,Senior Java Engineer,Acme,Austin,180000,2026-10-25T08:00:00Z,OPEN
            j2,Frontend Engineer,Globex,Austin,120000,2026-10-24T09:00:00Z,OPEN
            j3,Data Analyst,Initech,Remote,90000,2026-10-23T12:00:00Z,OPEN
            j4,Java Developer,Umbrella,Austin,150000,2026-10-22T15:00:00Z,CLOSED
            j5,Backend Engineer (Java),Hooli,Seattle,170000,2026-10-21T11:00:00Z,OPEN
            j6,QA Engineer,Acme,Austin,95000,2026-10-20T10:00:00Z,OPEN
            j7,Junior Java Developer,Globex,Austin,85000,2026-10-19T10:00:00Z,OPEN
            """;

    private final List<Job> jobs = new ArrayList<>();

    public JobRepository() {
        for (String line : SEED.strip().split("\n")) {
            jobs.add(parse(line));
        }
    }

    public List<Job> findAll() {
        return jobs;
    }

    public Optional<Job> findById(String id) {
        return jobs.stream().filter(j -> j.id().equals(id)).findFirst();
    }

    private static Job parse(String line) {
        String[] p = line.split(",");
        return new Job(p[0], p[1], p[2], p[3], Integer.parseInt(p[4]), Instant.parse(p[5]), p[6].trim());
    }
}
