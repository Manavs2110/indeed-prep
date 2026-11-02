import { useEffect, useState } from 'react';
import { getSavedSearches, deleteSavedSearch } from '../api/savedSearches';

export function SavedSearchesList() {
  // Show cached searches instantly, then refresh from the server
  const [searches, setSearches] = useState(JSON.parse(localStorage.getItem('savedSearches')) || []);

  useEffect(async () => {
    const data = await getSavedSearches();
    setSearches(data);
    localStorage.setItem('savedSearches', JSON.stringify(data));
  }, []);

  function handleDelete(id) {
    if (!window.confirm('Delete this saved search?')) return;
    deleteSavedSearch(id);
    setSearches(searches.filter((s) => s.id !== id));
  }

  return (
    <ul className="saved-searches">
      {searches.map((s) => (
        <li>
          <strong>{s.keyword}</strong> in {s.location || 'anywhere'} · {s.frequency === 0 ? 'Daily' : 'Weekly'}
          <button onClick={() => handleDelete(s.id)}>Delete</button>
        </li>
      ))}
    </ul>
  );
}
