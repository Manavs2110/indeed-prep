// SYMPTOM: Occasionally the Node server crashes with no useful log line,
// and some apply requests hang forever. (Express 4.)

app.post('/api/apply', async (req, res) => {
  const user = await getUser(req.userId);
  sendConfirmationEmail(user.email);
  await saveApplication(req.body);
  res.status(201).send();
});
