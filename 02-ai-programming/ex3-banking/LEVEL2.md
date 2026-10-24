# Ex. 3 · Level 2 — Top spenders

`List<String> topSpenders(long ts, int n)` returns up to `n` accounts as `"accountId(total)"`, where `total` is the sum of all money that has **left** the account through successful transfers.

Sort by total descending, then by `accountId` ascending. Accounts with a total of 0 are included.

Run: `javac *.java && java BankTest 2`
