package com.jobboard.savedjob;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "saved_jobs")
public class SavedJob {

    @Id
    @GeneratedValue
    private Long id;

    private String userId;
    private String jobId;
    private Date savedAt;

    public Long getId() { return id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    public Date getSavedAt() { return savedAt; }
    public void setSavedAt(Date savedAt) { this.savedAt = savedAt; }
}
