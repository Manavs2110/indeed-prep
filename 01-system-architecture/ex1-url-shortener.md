# Ex. 1 — URL shortener (45 min)

Design a service like bit.ly.

- Users submit a long URL and get a short one back.
- Visiting the short URL redirects to the long one.
- 100M new URLs per month; reads outnumber writes 10:1.
- Links should work for at least 5 years.

Deliverables: requirements, capacity estimate, API, data model, architecture diagram, one deep dive.

<details><summary>Twist A (reveal at ~20 min)</summary>

Users can choose a **custom alias** (`sho.rt/my-resume`). How do you guarantee uniqueness?
</details>

<details><summary>Twist B (reveal at ~30 min)</summary>

Marketing wants **click analytics per link**: total clicks, clicks per day, country. Near real-time is nice to have.
</details>

<details><summary>Twist C (reveal at ~40 min)</summary>

Links can have an **expiry date**. Expired links must return 404 and their aliases become reusable.
</details>
