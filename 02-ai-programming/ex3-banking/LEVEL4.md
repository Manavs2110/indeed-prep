# Ex. 3 · Level 4 — Merging accounts

`boolean mergeAccounts(long ts, String targetId, String sourceId)` merges the source account into the target:

- The source's balance is added to the target.
- The source's outgoing total is added to the target's (for `topSpenders`).
- The source's **pending** scheduled payments now belong to the target. They run from the target's balance and can be cancelled with the target's id.
- The source account is deleted. Its id can be used again by `createAccount`.

Return `false` (and change nothing) if the two ids are equal or either account doesn't exist.

Run: `javac *.java && java BankTest 4`
