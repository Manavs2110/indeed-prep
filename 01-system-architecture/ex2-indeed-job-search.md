# Ex. 2 — Indeed job search (60 min)

Design the core of Indeed's job search.

- Employers post, edit and close jobs.
- Job seekers search by **keyword** and **location** and see a ranked, paginated list.
- 300M monthly users, 30M active jobs.
- Search p99 latency under 300 ms.

<details><summary>Twist A — personalization</summary>

Results should be **personalized**: two people searching "software engineer, Austin" should see different rankings based on their history and profile.
</details>

<details><summary>Twist B — freshness</summary>

A new job must be **searchable within 1 minute** of the employer posting it.
</details>

<details><summary>Twist C — trust & safety</summary>

The trust team flags a **spam/scam job**. It must vanish from every search result **within seconds**, globally.
</details>

<details><summary>Twist D — autocomplete</summary>

Add **autocomplete** to the search box (both keyword and location), under 50 ms.
</details>
