// SYMPTOM: The "Applied" badge never shows on a job, even right after the
// user applied. Application and Job ids are both Strings loaded from the DB.

public boolean hasApplied(String seekerId, Job job) {
    for (Application a : applicationRepo.findBySeeker(seekerId)) {
        if (a.getJobId() == job.getId()) return true;
    }
    return false;
}
