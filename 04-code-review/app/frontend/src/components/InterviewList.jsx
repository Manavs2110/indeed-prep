import { useState, useEffect } from 'react';
import { getMyInterviews, cancelInterview } from '../api/interviews';
import { CountdownBadge } from './CountdownBadge';

export function InterviewList({ user }) {
  const [interviews, setInterviews] = useState([]);

  useEffect(() => {
    getMyInterviews(user.id).then(data => setInterviews(data));
  });

  function handleCancel(id) {
    cancelInterview(id);
    setInterviews(interviews.filter(i => i.id !== id));
  }

  if (interviews.length === 0) {
    return <p>You have no upcoming interviews.</p>;
  }

  return (
    <ul className="interview-list">
      {interviews
        .sort((a, b) => new Date(a.startTime) - new Date(b.startTime))
        .map((interview, index) => (
          <li key={index}>
            <h3>{interview.jobTitle}</h3>
            <p>{new Date(interview.startTime).toLocaleString()}</p>
            <CountdownBadge startTime={interview.startTime} />
            <a href={interview.meetingLink}>Join meeting</a>
            <div className="notes" dangerouslySetInnerHTML={{ __html: interview.notes }} />
            <button onClick={handleCancel(interview.id)}>Cancel interview</button>
          </li>
        ))}
    </ul>
  );
}
