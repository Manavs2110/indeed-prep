import { apiFetch } from './client';

export function searchJobs({ keyword, location, sort, page }) {
  return apiFetch(`/api/jobs/search?keyword=${keyword}&location=${location}&sort=${sort}&page=${page}`);
}

export function getSavedSearches() {
  return apiFetch('/api/saved-searches');
}

export function createSavedSearch(search) {
  return apiFetch('/api/saved-searches', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(search),
  });
}

export async function deleteSavedSearch(id) {
  await fetch(`/api/saved-searches/${id}`, { method: 'DELETE' });
}
