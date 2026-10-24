import java.time.Duration;
import java.time.Instant;

public class JobBadges {

    /** NEW = posted within the last 24 hours. */
    public static boolean isNew(Job job, Instant now) {
        Duration age = Duration.between(job.postedAt(), now);           // BUG-106: arguments were reversed (negative age)
        return !age.isNegative() && age.toHours() < 24;
    }
}
