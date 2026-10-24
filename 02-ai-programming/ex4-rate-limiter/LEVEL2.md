# Ex. 4 · Level 2 — Per-client limits

Add `void setLimit(String clientId, int maxRequests)` to `SlidingWindowLimiter`. It overrides the default `maxRequests` for that client from now on (paid plans get more). Requests already in the window still count.

Also think about (and be ready to discuss): memory per client, and what happens with 10M clients who each sent one request last week.

Run: `javac *.java && java RateLimiterTest 2`
