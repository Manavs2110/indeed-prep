import { formatSalary } from '../format';

export function JobCard({ job }) {
  return (
    <article className="job-card">
      <h3>{job.title}</h3>
      <p>{job.company} · {job.location}</p>
      <p>{formatSalary(job.salary)}</p>
    </article>
  );
}
