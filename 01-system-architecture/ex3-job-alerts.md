# Ex. 3 — Job alerts (60 min)

Users save a search ("nurse, Austin, $70k+") and get notified when **new matching jobs** are posted.

- Notification channels: email and mobile push.
- Jobs are posted continuously (assume ~1M new jobs/day).

<details><summary>Twist A</summary>

Support both **daily digest** and **instant** alerts, chosen per saved search.
</details>

<details><summary>Twist B</summary>

There are **50M saved searches**. Does your matching approach still work?
</details>

<details><summary>Twist C</summary>

A user must **never receive the same job twice**, even across digest and instant, and even if a job is edited and re-indexed.
</details>

<details><summary>Twist D</summary>

The email provider has a **2-hour outage**. No alerts may be lost.
</details>
