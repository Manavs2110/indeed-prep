## Add Save Job feature

Lets job seekers save jobs and view them on a new Saved Jobs page.

**Changes**
- Backend: `SavedJob` entity + repository, `SavedJobService`, `SavedJobController` (`POST/GET/DELETE /api/saved-jobs`)
- Frontend: `SaveJobButton` on every `JobCard`, new `SavedJobsPage` at `/saved`
- Users can save up to 100 jobs
- Sends a confirmation email when a job is saved

**Testing**
Tested locally, works.

Closes JOBS-1312
