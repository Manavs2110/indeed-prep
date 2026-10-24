import { Routes, Route } from 'react-router-dom';
import { SearchPage } from './pages/SearchPage';
import { JobDetailsPage } from './pages/JobDetailsPage';

export function App({ user }) {
  return (
    <Routes>
      <Route path="/" element={<SearchPage user={user} />} />
      <Route path="/jobs/:id" element={<JobDetailsPage user={user} />} />
    </Routes>
  );
}
