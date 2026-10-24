import java.util.*;
import java.util.concurrent.*;

/**
 * Reference solution: sliding log.
 * Exact, but O(maxRequests) memory per client. allow(): amortized O(1).
 * Thread safety: ConcurrentHashMap for the client map + a lock per client, so different
 * clients never contend. Discuss: sliding-window *counter* (two buckets, weighted) uses O(1)
 * memory at the cost of approximation; evict idle clients (e.g. a TTL cache) to bound memory.
 */
public class SlidingWindowLimiter {

    private static final class ClientState {
        final ArrayDeque<Long> log = new ArrayDeque<>();
        Integer limit; // null = default
    }

    private final int defaultMax;
    private final long windowMs;
    private final ConcurrentHashMap<String, ClientState> clients = new ConcurrentHashMap<>();

    public SlidingWindowLimiter(int maxRequests, long windowMs) {
        this.defaultMax = maxRequests;
        this.windowMs = windowMs;
    }

    // ---- Level 1 ----
    public boolean allow(String clientId, long tsMs) {
        ClientState s = clients.computeIfAbsent(clientId, k -> new ClientState());
        synchronized (s) {
            while (!s.log.isEmpty() && s.log.peekFirst() <= tsMs - windowMs) s.log.pollFirst();
            int limit = s.limit == null ? defaultMax : s.limit;
            if (s.log.size() >= limit) return false;
            s.log.addLast(tsMs);
            return true;
        }
    }

    // ---- Level 2 ----
    public void setLimit(String clientId, int maxRequests) {
        ClientState s = clients.computeIfAbsent(clientId, k -> new ClientState());
        synchronized (s) {
            s.limit = maxRequests;
        }
    }
}
