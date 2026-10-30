import { useState } from 'react';
import { scheduleInterview } from '../api/interviews';

export function ScheduleInterviewModal({ jobId, candidate, onClose, onScheduled }) {
  const [date, setDate] = useState('');
  const [time, setTime] = useState('');
  const [duration, setDuration] = useState(30);
  const [notes, setNotes] = useState('');

  async function handleSubmit(e) {
    e.preventDefault();
    const interview = await scheduleInterview({
      jobId,
      candidateId: candidate.id,
      candidateEmail: candidate.email,
      startTime: `${date}T${time}`,
      timezone: 'America/New_York',
      durationMinutes: parseInt(duration),
      notes,
    });
    onScheduled(interview);
    onClose();
  }

  return (
    <div className="modal">
      <form onSubmit={handleSubmit}>
        <h2>Schedule interview with {candidate.name}</h2>

        <label>
          Date
          <input type="date" value={date} onChange={e => setDate(e.target.value)} />
        </label>

        <label>
          Time
          <input type="time" value={time} onChange={e => setTime(e.target.value)} />
        </label>

        <label>
          Duration (minutes)
          <input type="number" value={duration} onChange={e => setDuration(e.target.value)} />
        </label>

        <label>
          Private notes (only visible to your team)
          <textarea value={notes} onChange={e => setNotes(e.target.value)} />
        </label>

        <button type="submit">Schedule</button>
        <button onClick={onClose}>Cancel</button>
      </form>
    </div>
  );
}
