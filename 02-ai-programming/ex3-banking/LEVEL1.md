# Ex. 3 — Banking system · Level 1

Every method takes a timestamp `ts` first. Timestamps are strictly increasing across calls. Amounts are positive integers.

| Method | Behavior |
| --- | --- |
| `boolean createAccount(long ts, String accountId)` | Create an account with balance 0. `false` if it already exists. |
| `Integer deposit(long ts, String accountId, int amount)` | Add money. Return the new balance, or `null` if the account doesn't exist. |
| `Integer transfer(long ts, String sourceId, String targetId, int amount)` | Move money. Return the source's new balance. Return `null` if either account is missing, source equals target, or the source has insufficient funds. |
| `Integer getBalance(long ts, String accountId)` | Current balance, or `null` if the account doesn't exist. |

Run: `javac *.java && java BankTest 1`
