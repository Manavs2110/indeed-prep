import { useState, useEffect } from 'react';

export function SavedJobsPage({ userId }) {
  const [jobs, setJobs] = useState([]);

  useEffect(() => {
    fetch(`/api/saved-jobs?userId=${userId}`)
      .then(res => res.json())
      .then(data => setJobs(data));
  });

  return (
    <div>
      <h1>Saved Jobs ({jobs.length})</h1>
      {jobs.map(job => (
        <div>
          <h3>{job.title}</h3>
          <p>{job.company} · {job.location}</p>
          <p>${job.salary}</p>
          <div dangerouslySetInnerHTML={{ __html: job.description }} />
        </div>
      ))}
    </div>
  );
}
