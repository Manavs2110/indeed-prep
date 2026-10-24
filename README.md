# Indeed SWE Onsite — Practice Exercises

Hands-on exercises for every Phase Two round. The study guide (frameworks, checklists, 14-day plan) lives in the companion doc; this repo is where you *do* the work.

| Folder | Round | What's inside | Time per exercise |
| --- | --- | --- | --- |
| `01-system-architecture/` | Systems Architecture | 5 design prompts; twists hidden in collapsible sections | 45–60 min |
| `02-ai-programming/` | Programming Exercise with AI | 4 multi-level Java problems with starter code + test suites | 50 min |
| `03-troubleshooting/` | Troubleshooting with AI | 4 bug reports with buggy code (React, Node, Java) + a hands-on drill | 10–60 min |
| `04-code-review/` | Code Review | A real PR on branch `pr/482-save-job` (Spring Boot + React) | 35 min |
| `05-resume-deep-dive/` | Resumé Deep Dive | Project-story template + STAR worksheet | 2–3 hrs once |
| `06-closing/` | Closing | Reflection prompt + question bank | 15 min |
| `answer-keys/` | — | Model answers and reference solutions. **Don't open until you've tried.** | — |

## Rules for practising

1. **Timebox.** Set a timer for the time shown. Stop when it rings, even if unfinished — that's real-interview data.
2. **Talk out loud.** Every round scores communication. Record yourself if you can.
3. **Levels unlock in order.** For AI programming, don't read Level N+1 until Level N passes.
4. **Use an AI assistant** for rounds 2 and 3 (that's the point). Use none for 1, 4, 5.
5. **Then check** `answer-keys/` and write down what you missed in `LOG.md`.

## Quick start

```bash
# AI programming: run tests for exercise 1
cd 02-ai-programming/ex1-in-memory-db && javac *.java && java InMemoryDbTest

# Code review: see the PR diff
git diff main...pr/482-save-job
```

To review the PR with real inline comments, push this repo to a private GitHub repo and open a PR from `pr/482-save-job` into `main` (see `04-code-review/README.md`).
