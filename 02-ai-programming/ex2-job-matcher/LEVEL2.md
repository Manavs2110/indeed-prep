# Ex. 2 · Level 2 — Matching

`List<String> match(String seekerId, int k)` returns up to `k` job IDs, best first.

A job is **eligible** for a seeker when all of these hold:
- its location equals the seeker's location (case-insensitive), **or** the job's location is `"Remote"` (any case)
- its salary is `>=` the seeker's `minSalary`
- it shares **at least one** skill with the seeker

Ranking:
1. More overlapping skills first
2. Then higher salary first
3. Then `jobId` ascending (lexicographic)

Unknown seeker → empty list.

Run: `javac *.java && java JobMatcherTest 2`
