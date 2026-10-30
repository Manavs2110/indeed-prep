# Code Review — PR #2 "Add bulk CSV job import and promoted listings" — answer key

Playbook IDs in brackets (C = correctness, T = concurrency, S = security, P = performance, E = errors/API, D = design, Q = tests/logs).

## Clarifying questions a strong reviewer asks first
- How do employer users authenticate? Should `employerId` come from `EmployerPrincipal` (like `ApplyController` uses `JobSeekerPrincipal`)?
- What's the max CSV size / row count? Should a bad row fail the whole import or be reported back?
- Should an import be all-or-nothing?
- Is promotion paid up front? Who verifies payment before `promoted = true`?
- What's the pricing rule at exactly 30 days, and is there a max discount?
- Is there a migration for the new `Job` columns? What happens to existing rows (null `employerId`)?

## Findings, ranked by severity

### Blocking

| # | Where | Issue | Fix |
| --- | --- | --- | --- |
| 1 | `JobImportController`, all endpoints [S2] | `employerId` comes from the URL path, so any employer can import into, read stats for, or promote jobs of **any other employer**. The team already has `EmployerPrincipal`. | `@AuthenticationPrincipal EmployerPrincipal user` → `user.employerId()`; drop it from the path (or verify it matches). |
| 2 | `promote` [S12, S2] | Saves the request-body `Job` wholesale (mass assignment): the client can set `salary`, `status`, `employerId`, `id` — even overwrite **another company's job** by putting its id in the body. The `jobId` path variable is ignored. | Load the job by `jobId`, check it belongs to the caller's employer, set only `promoted`/`promotionPrice`. Accept a small request DTO (`days`). |
| 3 | `saveRow` SQL [S1] | `employerId` and the CSV `title` are concatenated into SQL. A title like `x' OR '1'='1` breaks or abuses the query. | Parameterized query, or a repository method `existsByEmployerIdAndTitle`. |
| 4 | `importCsv` loop [C4] | `i <= lines.size()` → `lines.get(size)` throws `IndexOutOfBoundsException` **outside** the try, after all rows were already saved. Every import reports "Import failed" even though jobs were created; users retry and… (see #8). A header-only file also fails. | `i < lines.size()`. |
| 5 | `saveRow` `@Transactional` [Java: self-invocation] | `@Transactional` on a **private** method called from the same class does nothing (Spring proxies only intercept external public calls). Nothing is transactional; a partial import leaves half the rows saved. | Put `@Transactional` on the public `importCsv` (decide all-or-nothing vs per-row), or move per-row logic to another bean. |
| 6 | `stats` loop [C9] | `jobs.remove(j)` inside a for-each → `ConcurrentModificationException`. | Filter in the query: `findByEmployerId(employerId)`. |
| 7 | `stats` [C1, C5, C6] | `total += j.getSalary()` unboxes a **nullable** salary → NPE (the CSV explicitly allows empty salary). `total / jobs.size()` is integer division and divides by zero for an employer with no jobs. Also existing jobs have `employerId == null` → `j.getEmployerId().equals(...)` NPEs. | Query-side: `AVG(salary) WHERE employer_id = ? AND salary IS NOT NULL` returning a nullable `Double`; handle "no data". |
| 8 | `PromotionPricingService.price` [C5] | `discountPercent / 100` is integer division → always 0, so **the weekly discount is never applied** (14 days should be 10% off). Customers are overcharged vs the advertised price. | `price * discountPercent / 100.0`, with money in `BigDecimal`. |
| 9 | `price` [C8, S4] | Money as `double` (4.99 × days drifts); no validation of `days` (0 or negative → $0 / negative price); discount uncapped (140 days = 100% off = free, 147 days = negative price). | `BigDecimal` (or cents as `long`), validate `1 <= days <= max`, cap discount. |

### Major

| # | Where | Issue | Fix |
| --- | --- | --- | --- |
| 10 | `PriceKey` [C11] | `equals` without `hashCode` → `HashMap` lookups never hit; the cache is useless (and grows forever). | Use a `record`, or drop the cache — the calculation is trivial. |
| 11 | `priceCache` [T1, T3, T4] | `static HashMap` in a singleton: not thread-safe, unbounded, never invalidated (a price change needs a restart), per-instance. Keying by `employerId` makes it grow with every employer. | Remove it (cheap computation). If ever needed: Caffeine with TTL/size. |
| 12 | `DATE_FORMAT` [T7] | Static `SimpleDateFormat` is not thread-safe; concurrent imports garble dates or throw. `java.util.Date` + server time zone. | `LocalDate.parse(cols[3])` (ISO) / `DateTimeFormatter`; store `Instant`/`LocalDate`. |
| 13 | `totalImported` [T3, T6] | `static int` incremented non-atomically, per-instance, reset on restart — and exposed to every employer in `stats` (leaks platform-wide data). | Remove it; if needed, a metric (Micrometer counter) not an API field. |
| 14 | `saveRow` reindex call [P7, P8, D1] | New `RestTemplate` with no timeouts, hard-coded URL, synchronous per row inside the import (100 rows = 100 blocking calls; a hung indexer hangs the request thread). Ignores the existing `SearchIndexClient.enqueueReindex` (async with retries). | `searchIndexClient.enqueueReindex(job.getId())` after commit. |
| 15 | `importCsv` errors [E1] | `catch (Exception e) { // skip bad rows }` silently drops rows; the employer is told "Imported 7 jobs" with no idea which 3 failed or why. | Collect per-row errors (row number + reason) and return them in the response. |
| 16 | Controller `importJobs` [E3, E4, Q6] | Returns **200** on failure, includes `e.toString()` (internals) in the response, and `e.printStackTrace()` bypasses logging. | Throw `ValidationException` → `ApiExceptionHandler` (400); log with the logger. Return a structured result (201 + counts + row errors). |
| 17 | `split(",")` CSV parsing [C14] | Breaks on quoted fields (`"Engineer, Backend"`), trailing empty columns (`split` drops them → `cols[4]` `ArrayIndexOutOfBounds`), and a BOM in the header. No column-count check. | Use a CSV library (Apache Commons CSV / OpenCSV); validate column count per row. |
| 18 | `importCsv` input handling [P11, S4, E10] | `BufferedReader` never closed (use try-with-resources); whole file read into memory with no size/row limit; no charset (`InputStreamReader` uses platform default). | try-with-resources, `StandardCharsets.UTF_8`, stream rows, enforce a max size/row count. |
| 19 | Duplicate check [P1, T2] | One `COUNT(*)` query per row (N queries), and check-then-insert races with a concurrent import (double-click/retry, see #4) → duplicates. | Unique constraint on `(employer_id, title)` (if that's the rule) + batch insert; or load existing titles once into a `Set`. |
| 20 | `log.info(... row)` [S6, Q8] | Logs every row including `contactEmail` (PII) at INFO — one log line per row. | Log a summary (employerId, row count, failures) without PII. |
| 21 | `stats` `findAll()` [P2] | Loads every job in the platform to compute one employer's stats. | Aggregate query filtered by `employer_id`. |
| 22 | `promote` [E12, Q10] | Returns the entity (exposes `contactEmail`, internal fields). No payment check — promotion is granted for free. | Response DTO; integrate payment / require a paid order id. |
| 23 | `Job` new columns [E14] | No migration for 5 new columns; `promotionPrice` as primitive `double` and `promoted` as primitive `boolean` can't represent "unknown" and need defaults for existing rows. No index on `employer_id`, which every new query filters by [P4]. | Flyway/Liquibase migration with defaults/backfill + index on `employer_id`. |
| 24 | `JobImportServiceTest` [Q3, Q1] | Mocks the class under test, stubs `importCsv` to return 2, then asserts 2 — it can never fail and tests nothing. Would not catch #4, #6, #7, #8. No tests for pricing at all. | Real `JobImportService` with mocked repository/client; tests for header-only file, empty salary, bad row, duplicate; pricing tests at 7, 14, 30, 31 days. |

### Suggestions / nits

| # | Where | Issue |
| --- | --- | --- |
| 25 | `generateCouponCode` [S10, D11] | `java.util.Random` is predictable and 6 digits collide; also unused in this PR — remove or use `SecureRandom` + uniqueness check. |
| 26 | Pricing constants [D5] | `4.99`, `3.99`, `30`, `7`, `5` are magic numbers — named constants or config. `days > 30` vs `>= 30`: matches "over 30 days" in the description? Ask. |
| 27 | Field injection [D4] | `@Autowired` fields vs constructor injection used in `ApplyController`. |
| 28 | Controller → repository [D12] | `promote` puts business logic (pricing + persistence) in the controller; move to a `PromotionService`. |
| 29 | Praise | Clear PR description with ticket links; `UUID` ids; nullable salary handled at parse time. |

## Sample summary comment

> Thanks Priya — bulk import is a big win for large employers and the PR description is really clear. Requesting changes; the main blockers:
> 1. **Access control:** `employerId` from the path lets any employer import into / read / promote another employer's jobs, and `promote` saves the request body as-is (mass assignment, can overwrite any job). Could we use `EmployerPrincipal` and load the job by id?
> 2. **SQL injection** in the duplicate check (CSV title is concatenated into SQL).
> 3. **Runtime failures:** the `<=` loop makes every import report failure after saving; `@Transactional` on a private method is a no-op; `stats` throws `ConcurrentModificationException` and NPEs on null salaries.
> 4. **Pricing:** the weekly discount is never applied (integer division) and long promotions can go to $0 or negative.
>
> Other comments are suggestions. The current test mocks the service itself, so it can't fail — could we add real tests for the import edge cases and pricing boundaries? Happy to pair on the transaction/CSV parsing parts.

## Scoring guide
- **Strong pass:** #1–#9 found (at least 7 of 9) with clear impact and fixes, severity labels, summary comment.
- **Standout:** reading `main` first and using it — `EmployerPrincipal` (#1), `SearchIndexClient` (#14), `ApiExceptionHandler` (#16); spotting the private `@Transactional` (#5) and the tautological test (#24); quantifying impact ("14 days should be 10% off; we charge full price").
- **Weak:** style nits first, no labels, missing both security issues, "add a try/catch" style fixes.
