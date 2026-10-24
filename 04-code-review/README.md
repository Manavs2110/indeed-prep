# Code Review exercise — PR #482 "Add Save Job feature"

**Format (like Indeed's round):** ~10 min discussing review philosophy, then ~35 min writing a review of a teammate's PR. Spring Boot backend + React frontend.

## Setup

`main` holds the existing app (`app/`). The PR lives on branch **`pr/482-save-job`**.

```bash
git log --oneline main..pr/482-save-job       # the PR's commit
git diff --stat main...pr/482-save-job        # files changed
git diff main...pr/482-save-job               # full diff
```

**Recommended: real GitHub PR with inline comments.** Push this repo to a *private* GitHub repo, then open a PR from `pr/482-save-job` into `main`:

```bash
gh repo create indeed-prep --private --source=. --push
git push -u origin pr/482-save-job
gh pr create --base main --head pr/482-save-job --title "Add Save Job feature" --body-file 04-code-review/PR_DESCRIPTION.md
```

## The drill (35 min timer)

1. Read `PR_DESCRIPTION.md`. Say your clarifying questions out loud (write them down too).
2. Skim the **existing** code on `main` first — `app/backend/.../apply/ApplyController.java` and `app/frontend/src/api/client.js` show how the team already does things.
3. Find the entry points in the diff (controller endpoints, the React components) and follow the flow.
4. Read the whole diff once **without commenting**.
5. Second pass in priority order: correctness → security → performance → design → tests → style.
6. Leave labeled comments: **[Blocking] / [Suggestion] / [Question] / [Nit] / [Praise]** — each with *what*, *why*, *suggested fix*.
7. Finish with a summary comment: verdict, top 3 issues, one thing done well.

Then compare with `answer-keys/04-code-review.md`. Scoring guide inside.

## Philosophy warm-up (say each answer in under 60 s)

- What is a code review for?
- What do you look at first, and what do you leave to linters?
- You and the author disagree on a design choice and neither budges. What do you do?
- A senior engineer's PR has a serious bug. How do you raise it?
- How do you review AI-generated code differently?
- What makes a PR easy to review?
