import java.time.Duration;
import java.time.Instant;

public class JobBadges {

    /** NEW = posted within the last 24 hours. */
    public static boolean isNew(Job job, Instant now) {
        return Duration.between(now, job.postedAt()).toHours() < 24;
    }
}
