// SYMPTOM: Page 2 of results repeats the last job from page 1.
// (Also: what happens on page 1? Check the Postgres docs for OFFSET.)

app.get('/api/jobs', async (req, res) => {
  const page = parseInt(req.query.page) || 1;
  const size = 20;
  const jobs = await db.query(
    'SELECT * FROM jobs ORDER BY posted_at DESC LIMIT $1 OFFSET $2',
    [size, (page - 1) * size - 1]
  );
  res.json({ jobs });
});
