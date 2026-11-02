import { useState } from 'react';
import { createSavedSearch } from '../api/savedSearches';

export function SaveSearchButton({ keyword, location, initialSaved }) {
  const [saved, setSaved] = useState(initialSaved);
  const [frequency, setFrequency] = useState(0);

  async function handleSave() {
    setSaved(true);
    await createSavedSearch({ keyword, location, frequency });
  }

  return (
    <span className="save-search">
      <select value={frequency} onChange={(e) => setFrequency(e.target.value)}>
        <option value={0}>Daily</option>
        <option value={1}>Weekly</option>
      </select>
      <button onClick={handleSave}>{saved ? 'Saved ✓' : 'Save this search'}</button>
    </span>
  );
}
