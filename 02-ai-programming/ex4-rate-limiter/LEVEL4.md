# Ex. 4 · Level 4 — Thread safety

The gateway calls `allow` from many threads at once. Make **both** limiters thread-safe.

The test fires 16 threads × 2,000 calls across a handful of clients at the same timestamp and checks that exactly the limit gets through per client — no more, no fewer, no exceptions.

Don't just slap `synchronized` on everything without saying why. Be ready to explain the tradeoff of a global lock vs per-client locking vs lock-free approaches.

Run: `javac *.java && java RateLimiterTest 4`
