import java.util.concurrent.*;

/**
 * Reference solution: token bucket.
 * O(1) memory and time per client; allows bursts up to capacity.
 * Refill is computed lazily on each call instead of with a background timer.
 */
public class TokenBucketLimiter {

    private static final class Bucket {
        double tokens;
        long lastMs;

        Bucket(double tokens, long lastMs) { this.tokens = tokens; this.lastMs = lastMs; }
    }

    private final int capacity;
    private final double refillPerMs;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public TokenBucketLimiter(int capacity, double refillPerSecond) {
        this.capacity = capacity;
        this.refillPerMs = refillPerSecond / 1000.0;
    }

    // ---- Level 3 ----
    public boolean allow(String clientId, long tsMs) {
        Bucket b = buckets.computeIfAbsent(clientId, k -> new Bucket(capacity, tsMs));
        synchronized (b) {
            b.tokens = Math.min(capacity, b.tokens + (tsMs - b.lastMs) * refillPerMs);
            b.lastMs = tsMs;
            if (b.tokens < 1.0) return false;
            b.tokens -= 1.0;
            return true;
        }
    }
}
