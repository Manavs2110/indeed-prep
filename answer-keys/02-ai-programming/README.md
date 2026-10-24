# Programming with AI — reference solutions

Each folder has a reference `.java` file that passes all levels. To check: copy it next to the exercise's test file and run the tests.

| Exercise | Core design | Complexity | What to say in the debrief |
| --- | --- | --- | --- |
| Ex1 In-memory DB | `HashMap<key, TreeMap<field, Entry(value, expiresAt)>>` | get/set/delete O(log f); scan O(f); backup O(N) | TreeMap gives sorted scans and prefix range via `tailMap`. Backups store *remaining* TTL and deep-copy on both backup and restore so later writes can't leak in. |
| Ex2 Job matcher | Inverted index `skill → jobs`; count overlaps only over candidates; size-k heap | match O(C + C log k), C = candidates | A full scan takes ~33 s on Level 4; the index takes ~1 s. Next steps: index by (location, skill), and eager removal of closed jobs from postings. |
| Ex3 Banking | Min-heap of payments by `(executeAt, seq)`; `processDue(ts)` at the start of every method; lazy cancel | schedule O(log P); merge O(P) | Clarify: insufficient funds → skip. Payments due at the same time run in creation order (seq tie-break). Merge re-points pending payments. |
| Ex4 Rate limiter | Sliding log (deque per client) + token bucket (lazy refill); `ConcurrentHashMap` + per-client lock | O(1) amortized | Log = exact but O(limit) memory; window counter = O(1), approximate; bucket = O(1), allows bursts. Per-client locks avoid global contention. Evict idle clients. |

## Common mistakes the tests catch
- Ex1: record not removed when its last field is deleted; TTL boundary off by one (`<=` vs `<`); backup holding references to live maps.
- Ex2: skills not lowercased; duplicate skills double-counted; forgetting the `"Remote"` rule; tie-break order.
- Ex3: processing payments *after* the operation instead of before; failed schedule advancing the id counter.
- Ex4: counting denied requests; a token bucket that refills in whole tokens only; a plain `HashMap` (crashes under Level 4).
