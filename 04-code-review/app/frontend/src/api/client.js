// Shared API helper — sends the session cookie, throws ApiError on non-2xx.
export class ApiError extends Error {
  constructor(status, body) {
    super(`API ${status}`);
    this.status = status;
    this.body = body;
  }
}

export async function apiFetch(path, options = {}) {
  const res = await fetch(path, { credentials: 'include', ...options });
  if (!res.ok) throw new ApiError(res.status, await res.text());
  return res.status === 204 ? null : res.json();
}
