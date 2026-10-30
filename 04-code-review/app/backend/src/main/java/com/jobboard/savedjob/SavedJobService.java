package com.jobboard.savedjob;

import com.jobboard.email.EmailClient;
import com.jobboard.job.Job;
import com.jobboard.job.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SavedJobService {

    private static final int MAX_SAVED = 100;

    @Autowired private SavedJobRepository repo;
    @Autowired private JobRepository jobRepo;
    @Autowired private EmailClient emailClient;

    public void saveJob(String userId, String jobId) {
        List<SavedJob> existing = repo.findByUserId(userId);
        if (existing.size() > MAX_SAVED) {
            throw new RuntimeException("Too many saved jobs");
        }
        for (SavedJob s : existing) {
            if (s.getJobId() == jobId) {
                return;
            }
        }
        SavedJob saved = new SavedJob();
        saved.setUserId(userId);
        saved.setJobId(jobId);
        saved.setSavedAt(new Date());
        repo.save(saved);
        emailClient.send(userId, "You saved a job!");
    }

    public List<SavedJobDto> getSavedJobs(String userId) {
        List<SavedJobDto> result = new ArrayList<>();
        for (SavedJob s : repo.findByUserId(userId)) {
            Job job = jobRepo.findById(s.getJobId()).get();
            result.add(new SavedJobDto(job.getId(), job.getTitle(),
                    job.getCompany(), job.getSalary(), s.getSavedAt()));
        }
        return result;
    }

    public void unsave(String userId, String jobId) {
        repo.deleteByUserIdAndJobId(userId, jobId);
    }
}
