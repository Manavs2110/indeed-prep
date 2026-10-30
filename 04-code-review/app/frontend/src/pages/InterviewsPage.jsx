import { InterviewList } from '../components/InterviewList';

export function InterviewsPage({ user }) {
  return (
    <main>
      <h1>My interviews</h1>
      <InterviewList user={user} />
    </main>
  );
}
