package com.jobboard.savedjob;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {
    List<SavedJob> findByUserId(String userId);
    void deleteByUserIdAndJobId(String userId, String jobId);
}
