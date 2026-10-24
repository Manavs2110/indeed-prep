# Ex. 1 · Level 3 — Timestamps and TTL

Every operation now has a timestamp version. Timestamps are given in strictly increasing order across calls. Tests for this level and above only use the `...At` methods.

| Method | Behavior |
| --- | --- |
| `void setAt(key, field, value, long ts)` | Like `set`. The field never expires (overwriting a TTL field removes its TTL). |
| `void setAtWithTtl(key, field, value, long ts, long ttl)` | Like `set`, but the field is only visible for timestamps in `[ts, ts + ttl)`. |
| `String getAt(key, field, long ts)` | Like `get`, at time `ts`. Expired fields are invisible. |
| `boolean deleteAt(key, field, long ts)` | Like `delete`. Returns `false` for expired fields. |
| `List<String> scanAt(key, long ts)` | Like `scan`, excluding expired fields. |
| `List<String> scanByPrefixAt(key, prefix, long ts)` | Like `scanByPrefix`, excluding expired fields. |

Run: `javac *.java && java InMemoryDbTest 3`
