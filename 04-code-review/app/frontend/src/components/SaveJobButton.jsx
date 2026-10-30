import { useState } from 'react';

export function SaveJobButton({ job, userId }) {
  const [saved, setSaved] = useState(false);

  const handleClick = () => {
    setSaved(!saved);
    if (saved) {
      fetch(`/api/saved-jobs/${job.id}?userId=${userId}`, { method: 'DELETE' });
    } else {
      fetch(`/api/saved-jobs/${job.id}?userId=${userId}`, { method: 'POST' });
    }
  };

  return (
    <div onClick={handleClick} className="save-btn">
      {saved ? '★ Saved' : '☆ Save'}
    </div>
  );
}
