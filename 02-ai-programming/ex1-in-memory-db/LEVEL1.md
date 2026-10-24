# Ex. 1 — In-memory database · Level 1

Implement a simple in-memory database of **records**. Each record is identified by a `key` and holds `field → value` pairs (all strings).

| Method | Behavior |
| --- | --- |
| `void set(String key, String field, String value)` | Insert or overwrite the field in the record (create the record if needed). |
| `String get(String key, String field)` | Return the value, or `null` if the record or field doesn't exist. |
| `boolean delete(String key, String field)` | Remove the field. Return `true` if it existed, else `false`. A record with no fields left should no longer exist. |

Run: `javac *.java && java InMemoryDbTest 1`
