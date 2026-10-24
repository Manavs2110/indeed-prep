# Ex. 1 · Level 4 — Backup and restore

| Method | Behavior |
| --- | --- |
| `int backup(long ts)` | Save a snapshot of the database at `ts`. Return the number of records that have at least one non-expired field. For TTL fields, the snapshot stores the **remaining** TTL at `ts`, not the absolute expiry. |
| `void restore(long ts, long tsToRestore)` | Replace the database with the **latest** backup taken at or before `tsToRestore`. Restored TTL fields expire at `ts + remainingTtl`. It's guaranteed that such a backup exists. |

Think about: does a later mutation affect an earlier backup? (It must not.)

Run: `javac *.java && java InMemoryDbTest 4`
