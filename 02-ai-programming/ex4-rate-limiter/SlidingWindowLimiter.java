import java.util.*;

public class SlidingWindowLimiter {

    public SlidingWindowLimiter(int maxRequests, long windowMs) { }

    // ---- Level 1 ----
    public boolean allow(String clientId, long tsMs) { throw new UnsupportedOperationException(); }

    // ---- Level 2 ----
    public void setLimit(String clientId, int maxRequests) { throw new UnsupportedOperationException(); }
}
