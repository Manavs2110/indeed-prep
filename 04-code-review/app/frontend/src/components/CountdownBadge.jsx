import { useState, useEffect } from 'react';

export function CountdownBadge({ startTime }) {
  const [minutesLeft, setMinutesLeft] = useState(null);

  useEffect(() => {
    setInterval(() => {
      const diff = new Date(startTime) - new Date();
      setMinutesLeft(Math.floor(diff / 60000));
    }, 1000);
  }, []);

  if (minutesLeft === null) return null;

  if (minutesLeft < 60) {
    return <span className="badge urgent">Starts in {minutesLeft} min</span>;
  }
  return <span className="badge">In {Math.floor(minutesLeft / 60)} hours</span>;
}
