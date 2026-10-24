# JobBoard — troubleshooting drill (60 min)

You've just joined the team that owns JobBoard's search and apply backend. Six bug tickets came in overnight (`BUG_REPORTS.md`). You've never seen this code.

## Run it

```bash
javac -d out src/*.java && java -cp out Main            # demo: what users see
javac -d out src/*.java && java -cp out JobBoardTest    # regression tests (currently failing)
```

## Rules

- Start your timer. Read the tickets, **not** the source files, first.
- Use your AI assistant, but narrate: what you're giving it, why, and whether you trust its answer.
- For each bug, write in `NOTES.md`: symptom → root cause (one sentence) → fix → how you verified.
- Fix the root cause, not the test. Don't edit `JobBoardTest.java`.
- Stretch: once all tests pass, look for a **7th** problem nobody reported yet.
