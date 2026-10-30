// Interview API calls

export async function scheduleInterview(data) {
  const res = await fetch('/api/interviews', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  });
  return res.json();
}

export async function getMyInterviews(candidateId) {
  const res = await fetch(`/api/interviews/mine?candidateId=${candidateId}`);
  return res.json();
}

export async function cancelInterview(id) {
  await fetch(`/api/interviews/${id}/cancel`, { method: 'POST' });
}
