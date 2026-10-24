import { formatSalary } from '../format';
import { SaveJobButton } from './SaveJobButton';

export function JobCard({ job }) {
  return (
    <article className="job-card">
      <h3>{job.title}</h3>
      <p>{job.company} · {job.location}</p>
      <p>{formatSalary(job.salary)}</p>
      <SaveJobButton job={job} userId={user.id} />
    </article>
  );
}
