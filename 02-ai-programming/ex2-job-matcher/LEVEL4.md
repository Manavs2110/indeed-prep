# Ex. 2 · Level 4 — Scale

The test now loads **200,000 jobs** (from 500 distinct skills across 20 cities + Remote) and **2,000 seekers**, then calls `match(seeker, 10)` for every seeker.

The whole level must finish in **under 3 seconds**. A full scan of every job per `match` call won't make it.

Redesign so `match` only looks at jobs that could possibly be eligible. Before you prompt the AI, decide the data structure yourself and say why. Be ready to state the complexity of `addJob` and `match` afterward.

Run: `javac *.java && java JobMatcherTest 4`
