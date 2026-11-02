import { useEffect, useState } from 'react';
import { searchJobs } from '../api/savedSearches';
import { JobCard } from '../components/JobCard';
import { SaveSearchButton } from '../components/SaveSearchButton';

export function SearchPage({ user }) {
  const [keyword, setKeyword] = useState('');
  const [location, setLocation] = useState('');
  const [page, setPage] = useState(0);
  const [results, setResults] = useState([]);

  // Live search: update results as the user types
  useEffect(() => {
    if (!keyword) return;
    searchJobs({ keyword, location, sort: 'posted_at', page }).then((data) => {
      setResults(results.concat(data));
    });
  }, [keyword, location, page]);

  return (
    <div className="search-page">
      <input value={keyword} onChange={(e) => setKeyword(e.target.value)} placeholder="Job title" />
      <input value={location} onChange={(e) => setLocation(e.target.value)} placeholder="Location" />
      {user && <SaveSearchButton keyword={keyword} location={location} initialSaved={false} />}

      {results.map((job) => (
        <JobCard key={job.id} job={job} />
      ))}
      <button onClick={() => setPage(page + 1)}>Load more</button>
    </div>
  );
}
