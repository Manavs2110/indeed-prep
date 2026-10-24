# Ex. 2 · Level 3 — Applying and closing

| Method | Behavior |
| --- | --- |
| `boolean apply(String seekerId, String jobId)` | Record an application. Return `false` if the seeker or job doesn't exist, the job is closed, or the seeker already applied to it. |
| `boolean closeJob(String jobId)` | Close a job. Return `false` if the job doesn't exist or is already closed. |

Jobs the seeker has applied to, and closed jobs, are no longer returned by `match`.

Run: `javac *.java && java JobMatcherTest 3`
