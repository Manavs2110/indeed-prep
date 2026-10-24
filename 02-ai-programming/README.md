# Programming Exercise with AI

**How Indeed runs it:** an ambiguous, multi-part build (an "engine"/system) done *with* an AI assistant, tests to pass in a timebox, then you explain the solution. They score how well you drive the AI while fully understanding the code.

## How to practise each exercise (50 min timer)

1. Open the exercise folder and read **only `LEVEL1.md`**.
2. Say your clarifying questions out loud, then write your design (data structures + complexity) in a comment *before* prompting the AI.
3. Use your AI assistant (Claude Code, Copilot, Cursor, ChatGPT...) to implement **one level at a time**. Read every line it writes.
4. Run the tests for that level:
   ```bash
   javac *.java && java <TestClass> 1     # level 1 only
   javac *.java && java <TestClass> 2     # levels 1-2
   ```
5. Only when a level is green, open the next `LEVELn.md`.
6. Last 5 minutes: close the AI and explain your final code + Big-O of every method out loud.

Requires Java 17+. No build tool needed. Starter files contain every method signature as a stub — the `LEVELn.md` files describe what they must do.

| Exercise | Engine | Test class |
| --- | --- | --- |
| `ex1-in-memory-db/` | Key-field-value store with TTL and backups | `InMemoryDbTest` |
| `ex2-job-matcher/` | Indeed-style job matching with a perf level | `JobMatcherTest` |
| `ex3-banking/` | Accounts, transfers, scheduled payments, merges | `BankTest` |
| `ex4-rate-limiter/` | Sliding window + token bucket, thread-safe | `RateLimiterTest` |

Reference solutions: `answer-keys/02-ai-programming/` — compare *after* you finish, especially the design choices.

## Self-score after each run

| Criterion | 1–5 |
| --- | --- |
| Clarified ambiguity before coding | |
| Designed before prompting | |
| Prompts were specific and scoped | |
| Read/understood every generated line | |
| Tested edge cases beyond the given tests | |
| Could explain complexity with the AI closed | |
