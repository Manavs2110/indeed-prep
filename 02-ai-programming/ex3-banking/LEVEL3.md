# Ex. 3 · Level 3 — Scheduled payments

| Method | Behavior |
| --- | --- |
| `String schedulePayment(long ts, String accountId, int amount, long delay)` | Schedule a withdrawal at `ts + delay`. Return an id `"payment1"`, `"payment2"`, ... (a global counter that only advances on success). Return `null` if the account doesn't exist. |
| `boolean cancelPayment(long ts, String accountId, String paymentId)` | Cancel a pending payment. `true` only if it exists, is still pending, and belongs to `accountId`. |

Execution rules:
- At the **start** of every operation with timestamp `ts`, first run every pending payment whose execution time is `<= ts`.
- Payments due at the same time run in creation order.
- If the account has insufficient funds when a payment runs, it is **skipped** and discarded.
- Executed payments count towards the account's `topSpenders` total.

Run: `javac *.java && java BankTest 3`
