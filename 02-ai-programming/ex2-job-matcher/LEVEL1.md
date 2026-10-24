# Ex. 2 — Job matcher · Level 1

Build the core of a job-matching engine for a job site.

| Method | Behavior |
| --- | --- |
| `boolean addJob(String jobId, String title, List<String> skills, String location, int salary)` | Add a job. Return `false` (and change nothing) if `jobId` already exists. |
| `boolean addSeeker(String seekerId, List<String> skills, String location, int minSalary)` | Add a job seeker. Return `false` if `seekerId` already exists. |

Skills are **case-insensitive** (`"Java"` and `"java"` are the same skill). Duplicate skills in a list count once.

Run: `javac *.java && java JobMatcherTest 1`
