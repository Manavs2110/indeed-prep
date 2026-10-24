import { Routes, Route } from 'react-router-dom';
import { SearchPage } from './pages/SearchPage';
import { JobDetailsPage } from './pages/JobDetailsPage';
import { SavedJobsPage } from './pages/SavedJobsPage';

export function App({ user }) {
  return (
    <Routes>
      <Route path="/" element={<SearchPage user={user} />} />
      <Route path="/jobs/:id" element={<JobDetailsPage user={user} />} />
      <Route path="/saved" element={<SavedJobsPage userId={user.id} />} />
    </Routes>
  );
}
