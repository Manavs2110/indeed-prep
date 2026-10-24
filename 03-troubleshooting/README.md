# Troubleshooting with AI

**How Indeed runs it:** you get an unfamiliar codebase (Node.js/React or Java/React) and bug reports, and you diagnose and fix them with an AI assistant. They score your *systematic* root-cause process, the precise context you give the AI, and how critically you check its fixes.

## The loop to practise every time

1. **Reproduce** — run it, see the symptom, state expected vs actual in one sentence.
2. **Orient** — find entry points; have the AI summarize structure (≤ 5 min).
3. **Narrow** — follow the data through the layers until it first goes wrong.
4. **Hypothesize out loud** — then prove it (log line, breakpoint, failing test).
5. **Fix with AI** — exact file + function + evidence + constraint ("change only this").
6. **Verify** — rerun repro + tests; ask "where else is this pattern?"

## Exercises

| Exercise | What | Time |
| --- | --- | --- |
| `jobboard/` | A small multi-file Java job-board codebase with **6 bugs** reported by users. Main drill. | 60 min |
| `snippets/` | 4 short React / Node / Java bugs with symptoms. Warm-up, no AI. | 10 min each |
| Hands-on with a real app | Clone a RealWorld "Conduit" app in your stack; have a friend (or an AI in a separate chat) plant 3 bugs. | 60 min |

Answer key: `answer-keys/03-troubleshooting.md` and fixed code in `answer-keys/03-troubleshooting/jobboard-fixed/`.
