# Troubleshooting — answer key

## JobBoard (fixed code in `03-troubleshooting/jobboard-fixed/`)

| Ticket | Root cause | Fix |
| --- | --- | --- |
| BUG-101 | `SearchService.search` lowercases the title but not the query, so `"Java"` never matches. | `query.toLowerCase(Locale.ROOT)` once, compare lowercased. |
| BUG-102 | The API's `page` is 1-based but `from = page * pageSize` treats it as 0-based, so page 1 skips the newest results. | `from = (page - 1) * pageSize`, and validate `page >= 1`. |
| BUG-103 | `search` calls `removeIf` on the list returned by `JobRepository.findAll()`, which is the repository's own internal list, so every search permanently deletes jobs. | Filter with a stream instead of mutating. Defense in depth: `findAll()` returns `List.copyOf(jobs)`. |
| BUG-104 | `ApplicationService` uses `status() != "OPEN"`. `==` compares String references, and statuses parsed from data are new String objects, so every job looks closed. | `"OPEN".equals(status)`. Better: a `JobStatus` enum. Note that `SearchService` already uses `.equals`; inconsistent code is a clue. |
| BUG-105 | `medianSalary` takes the middle element of an **unsorted** list. | `Collections.sort(salaries)` first. |
| BUG-106 | `Duration.between(now, postedAt)` has its arguments reversed, so the age is negative and always `< 24h`. | `Duration.between(postedAt, now)`, and treat negative (future-dated) as not new. |

**How the bugs hide each other:** in `Main`, BUG-101 + BUG-103 make *every* later search empty, which looks like one bug. Separating them (fresh repository per test, or reordering the demo) is the systematic move interviewers want to see.

**Stretch (7th problem):** after fixing BUG-102, `page = 0` gives a negative `from` and `subList` throws `IndexOutOfBoundsException`, so validate it. Also: a `null` query NPEs, and `ApplicationService` uses a non-thread-safe `HashMap` in what would be a singleton service.

**Process points interviewers score:**
- Reproduced each ticket before reading code (`Main`, tests).
- Gave the AI the ticket + the specific file + the observed output, not "fix my code".
- Proved each hypothesis (a print, a breakpoint, a test) before changing code.
- Asked "where else does this pattern appear?" (e.g. other `==` String comparisons, other callers of `findAll()`).
- Fixed root causes, not tests.

## Snippets

| Ex. | Root cause | Minimal fix |
| --- | --- | --- |
| 1 | Race: one fetch per keystroke, responses arrive out of order and an old response overwrites a newer one. Also `key={i}` and the query isn't URL-encoded. | `let cancelled = false` with a cleanup that sets it to `true` (or `AbortController`); only `setJobs` if not cancelled. `key={j.id}`, `encodeURIComponent(query)`, optional debounce. |
| 2 | OFFSET is `(page - 1) * size - 1`: page 2 gives 19 (repeats job #20); page 1 gives −1, which Postgres rejects. Ordering only by non-unique `posted_at` is unstable. | `(page - 1) * size`; `ORDER BY posted_at DESC, id DESC`; clamp `page >= 1`. |
| 3 | `==` compares String references, so it's false for equal ids loaded separately. | `.equals()` / `Objects.equals()`. Better: `existsBySeekerIdAndJobId` instead of loading all applications. |
| 4 | Express 4 doesn't catch async rejections, so the request hangs. The un-awaited `sendConfirmationEmail` rejection crashes Node 15+. `user` may be null. The email is sent before the save. | `try/catch` + `next(err)` (or `express-async-errors`); 404 if no user; save first; send the email via a queue or with `.catch(log)`; validate the body. |
