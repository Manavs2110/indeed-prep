# Ex. 1 · Level 2 — Scans

| Method | Behavior |
| --- | --- |
| `List<String> scan(String key)` | All fields of the record as `"field(value)"`, sorted by field name (lexicographic). Empty list if the record doesn't exist. |
| `List<String> scanByPrefix(String key, String prefix)` | Same, but only fields whose name starts with `prefix`. |

Run: `javac *.java && java InMemoryDbTest 2`
