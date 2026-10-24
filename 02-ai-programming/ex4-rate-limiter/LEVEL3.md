# Ex. 4 · Level 3 — Token bucket

Implement `TokenBucketLimiter(int capacity, double refillPerSecond)`.

| Method | Behavior |
| --- | --- |
| `boolean allow(String clientId, long tsMs)` | A new client starts with a full bucket (`capacity` tokens). Before deciding, add `elapsedMs / 1000 * refillPerSecond` tokens since that client's last call, capped at `capacity`. If at least 1 token is available, take 1 and return `true`; else return `false`. |

Be ready to compare: sliding log vs sliding-window counter vs token bucket — accuracy, memory, burst behavior.

Run: `javac *.java && java RateLimiterTest 3`
