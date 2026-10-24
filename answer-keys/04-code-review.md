# Code Review — PR #482 answer key

## Clarifying questions a strong reviewer asks first
- Should identity come from the session principal like `ApplyController` does?
- Is the confirmation email a product requirement? Email calls are synchronous (300–800 ms).
- What should happen to saved jobs when a job is closed or deleted? Can users save closed jobs?
- Should the Save button reflect the real saved state on page load?
- Expected volume: how many saved jobs per user, and how often is the Saved page loaded?

## Findings, ranked by severity

| # | Where | Severity | Issue | Suggested fix |
| --- | --- | --- | --- | --- |
| 1 | `SavedJobController`, all endpoints | Blocking | `userId` comes from a query param, so any user can read, save or delete another user's saved jobs (IDOR). It also breaks the team's convention in `ApplyController`. | `@AuthenticationPrincipal JobSeekerPrincipal user` → `user.id()`; drop the param. |
| 2 | `JobCard.jsx` | Blocking | It now uses `user.id`, but `user` isn't a prop of `JobCard`. Every card throws `TypeError`, so the search page crashes. ("Tested locally"?) | Pass `user` down, or better, drop `userId` entirely once #1 is fixed. |
| 3 | `SavedJobsPage.jsx` | Blocking | `dangerouslySetInnerHTML` with employer-written `description`. `Job.java` says it's sanitized only by `JobDetailsPage`, so this is stored XSS. | Don't render it here, or reuse the sanitizer. |
| 4 | `SavedJobsPage.jsx` `useEffect` | Blocking | No dependency array, so every `setJobs` re-renders and refetches: an infinite request loop. | `useEffect(..., [])` (or `[userId]` until #1), plus cleanup/abort. |
| 5 | `SavedJobService.getSavedJobs` | Blocking | `findById(...).get()` throws `NoSuchElementException` for a deleted job, so one stale row 500s the whole page. | Skip missing jobs, or show "no longer available"; consider FK/cascade. |
| 6 | `SavedJobService.unsave` | Blocking | A derived `deleteBy...` query needs a transaction; without `@Transactional` it throws `InvalidDataAccessApiUsageException` at runtime. | `@Transactional` on the method (or the service). |
| 7 | `SavedJobService.saveJob` | Blocking | `s.getJobId() == jobId` compares String references, so it's almost always false and duplicates are saved. | `.equals()`, or better `repo.existsByUserIdAndJobId(...)`. |
| 8 | `SavedJob` entity + service | Major | Check-then-insert race, and no unique constraint on `(userId, jobId)`. Double clicks create duplicates. No index on `userId`, which every call filters by. | `@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"userId","jobId"}), indexes = @Index(columnList = "userId"))`; treat a constraint violation as "already saved". |
| 9 | `SavedJobController.saveJob` | Major | `catch (Exception)` returns **200** with `e.getMessage()`, so failures look like success and internals leak. The team already maps exceptions in `ApiExceptionHandler`. | Throw domain exceptions; let the handler map them to 404/409/422. |
| 10 | `SavedJobService.saveJob` | Major | `existing.size() > MAX_SAVED` allows 101, and it loads every saved row just to count. | `repo.countByUserId(id) >= MAX_SAVED`. |
| 11 | `SavedJobService.saveJob` | Major | A synchronous email on every save adds 300–800 ms. If the email fails after `save`, the controller reports an error although the job *was* saved. | Confirm the requirement. If kept, publish an event and send async after commit. |
| 12 | `SavedJobService.getSavedJobs` | Major | N+1 query (one `findById` per saved job, up to 101 queries) and no pagination. | `jobRepo.findAllById(ids)` or a join query; page and sort by `savedAt`. |
| 13 | `SavedJobService.saveJob` | Major | Never checks that the job exists, so arbitrary ids are stored. | `jobRepo.existsById` → 404. Ask whether closed jobs can be saved (`JobStatus`). |
| 14 | Front/back contract | Major | `SavedJobsPage` reads `job.description` and `job.location`, but `SavedJobDto` has neither. | Align the DTO and the UI; consider a shared OpenAPI schema/types. |
| 15 | `SaveJobButton.jsx` | Major | Initial state is always `false`, so already-saved jobs show "Save" and re-POST. Optimistic toggle with no error check or rollback; rapid clicks race. | Initialize from the server (`isSaved` on the job, or the saved-ids set); await, roll back on error, disable while pending. |
| 16 | Both React files | Major | Raw `fetch` bypasses the team's `apiFetch` helper, so no `credentials: 'include'` (requests are unauthenticated cross-origin) and errors are never thrown. | Use `apiFetch`. |
| 17 | `SaveJobButton.jsx` | Minor | A clickable `<div>` isn't keyboard- or screen-reader-accessible. | `<button aria-pressed={saved}>`. |
| 18 | `SavedJobsPage.jsx` | Minor | No loading, error or empty state; missing `key` on list items. | Add states; `key={job.id}`. |
| 19 | `SavedJobsPage.jsx` | Minor | `${job.salary}` shows `$null` for jobs without a salary; `formatSalary` already exists. | Reuse `formatSalary`. |
| 20 | `SavedJob` / service | Minor | `java.util.Date` and a generic `RuntimeException`. | `Instant`; `SavedJobLimitException`. |
| 21 | Service + controller | Nit | Field injection (`@Autowired`), unlike the constructor injection in `ApplyController`. | Constructor injection with final fields. |
| 22 | PR overall | Major | No tests, and the description says "tested locally" while #2 and #6 crash. | Service tests (duplicate, limit, deleted job), a controller auth test, a component test for the button. |

## Sample summary comment

> Thanks for picking this up! The feature is well scoped and the backend/frontend split is easy to follow. Requesting changes for a few blocking issues:
> 1. **Security:** user identity comes from a query param (any user can access another's saved jobs), and `SavedJobsPage` renders unsanitized employer HTML (XSS).
> 2. **Runtime failures:** `JobCard` references an undefined `user` (search page crashes), the `useEffect` has no deps (infinite fetch loop), `.get()` throws on deleted jobs, and `deleteBy` needs `@Transactional`.
> 3. **Data integrity:** duplicate detection uses `==` and has no DB unique constraint.
>
> The other comments are suggestions. Could we add service tests for the duplicate, limit and deleted-job cases? Happy to pair on any of this.

## Scoring guide
- **Strong pass:** finds #1–#7 and explains them clearly and kindly, with severity labels and a summary.
- **Standout signals:** noticing the PR ignores existing conventions (#1, #9, #16, #19, #21). That's only visible if you read `main` first, which is the "code is part of a larger application" hint. Also #2 (needs reading the whole diff) and #14 (full-stack contract thinking).
- **Weak signals:** 20 nits with no ranking, harsh tone ("this is wrong"), commenting before understanding the flow, or missing all security issues.
