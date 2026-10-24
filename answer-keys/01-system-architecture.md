# Systems Architecture — what strong answers cover

## Ex. 1 URL shortener
- **Estimates:** 100M/mo ≈ 40 writes/s, 400 reads/s (peak ~5x). 5 yrs ≈ 6B URLs. Base62 × 7 chars ≈ 3.5T codes.
- **IDs:** counter ranges handed to each server (or Snowflake IDs) → base62. No collisions, no lookups. Hash+check is the alternative.
- **Storage:** KV store (DynamoDB/Cassandra) keyed by code; Redis for hot links; CDN optional.
- **Redirect:** 302 if you want analytics (301 is cached by browsers).
- **A, custom alias:** conditional put ("insert if absent"); reserve words.
- **B, analytics:** click events → Kafka → stream aggregation → OLAP (ClickHouse/Druid). Never on the redirect's critical path.
- **C, expiry:** TTL field checked on read + background sweeper; reuse alias only after hard delete.

## Ex. 2 Indeed job search
- **Estimates:** ~100M searches/day ≈ 1.2k QPS avg, ~5k peak. 30M jobs × ~5 KB ≈ 150 GB → sharded + replicated Elasticsearch.
- **Core:** Job service → Postgres (source of truth) → outbox/CDC → Kafka → indexer → Elasticsearch. Search service: BM25 relevance + geo filter + pagination; short-TTL Redis cache for popular queries.
- **A, personalization:** two-stage ranking. ES retrieves ~500 candidates; a ranking service re-scores with user features (feature store, built from click/apply events, batch + streaming). Cache candidates per query, not results per user.
- **B, 1-minute freshness:** streaming indexing via CDC; ES refresh ~1 s; alert on index lag.
- **C, spam takedown in seconds:** high-priority delete event **and** a query-time blocklist (Redis set) filtered in the search service, since index and caches lag. Purge cache entries.
- **D, autocomplete:** separate service; top-k completions per prefix built daily from query logs, served from memory (trie) or the ES completion suggester; debounce client-side; CDN cache.
- **Prod readiness:** p99 latency, zero-result rate, index lag, scraper rate limiting, multi-region read replicas, canary deploys.

## Ex. 3 Job alerts
- **Digest:** nightly batch partitions saved searches across workers; each runs its query against ES with `posted_at > last_run`.
- **Instant:** reverse search. Each new job from Kafka is matched against stored queries (ES percolator / inverted index over query terms).
- **B, 50M searches:** dedupe identical queries (thousands save "nurse, Austin"), run each unique query once, fan out to subscribers.
- **C, no duplicates:** `sent(user_id, job_id)` store (Cassandra/Redis with TTL) checked before send; idempotency key per notification; job edits don't create new ids.
- **D, provider outage:** durable queue + retries with exponential backoff + DLQ + fallback provider. At-least-once + dedupe = effectively once.
- **Also:** per-user rate limits, unsubscribe compliance, open and alert-to-apply metrics.

## Ex. 4 Apply flow
- Browser uploads directly to S3 via presigned URL; S3 event → async virus-scan worker; quarantine bucket until clean.
- Applications in Postgres with unique `(seeker_id, job_id)`; status changes as an audited state machine.
- **C, 10k applies/hr** is only ~3 writes/s, easy. The hard part is the employer side: paginated dashboard from a read replica, batched notifications, async resume parsing.
- **B, GDPR:** inventory every PII location (DB, S3, search index, logs, analytics, backups); deletion pipeline with tracking; crypto-shredding (delete per-user key) covers backups.

## Interviewer's rubric (score yourself 1–5)
Clarified requirements · estimated before designing · justified each technology · tradeoffs spoken aloud · adapted to twists without restarting · covered failure modes and production readiness.
