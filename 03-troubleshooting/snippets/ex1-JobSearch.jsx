// SYMPTOM: Search results flicker while typing, and sometimes show results
// for an earlier query than the one in the box.

function JobSearch() {
  const [query, setQuery] = useState('');
  const [jobs, setJobs] = useState([]);

  useEffect(() => {
    fetch(`/api/jobs?q=${query}`)
      .then(r => r.json())
      .then(data => setJobs(data.jobs));
  }, [query]);

  return (
    <>
      <input value={query} onChange={e => setQuery(e.target.value)} />
      {jobs.map((j, i) => <JobCard key={i} job={j} />)}
    </>
  );
}
