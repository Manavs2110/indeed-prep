# Ex. 4 — Rate limiter · Level 1

Build a rate-limiting library for an API gateway.

`SlidingWindowLimiter(int maxRequests, long windowMs)`

| Method | Behavior |
| --- | --- |
| `boolean allow(String clientId, long tsMs)` | Return `true` if the client has had fewer than `maxRequests` **allowed** requests in the window `(tsMs - windowMs, tsMs]`, and record this request. Otherwise return `false`. Denied requests don't count. |

Timestamps for a client never go backwards. Each client is limited independently.

Run: `javac *.java && java RateLimiterTest 1`
