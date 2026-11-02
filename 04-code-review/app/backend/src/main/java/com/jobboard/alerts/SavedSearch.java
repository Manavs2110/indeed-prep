package com.jobboard.alerts;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "saved_searches")
public class SavedSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String keyword;
    private String location;
    private Integer minSalary;
    private AlertFrequency frequency;
    private Date lastSentAt;
    private String unsubscribeToken;

    public Long getId() { return id; }
    public String getUserId() { return userId; }
    public String getKeyword() { return keyword; }
    public String getLocation() { return location; }
    public Integer getMinSalary() { return minSalary; }
    public AlertFrequency getFrequency() { return frequency; }
    public Date getLastSentAt() { return lastSentAt; }
    public String getUnsubscribeToken() { return unsubscribeToken; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    public void setLocation(String location) { this.location = location; }
    public void setMinSalary(Integer minSalary) { this.minSalary = minSalary; }
    public void setFrequency(AlertFrequency frequency) { this.frequency = frequency; }
    public void setLastSentAt(Date lastSentAt) { this.lastSentAt = lastSentAt; }
    public void setUnsubscribeToken(String unsubscribeToken) { this.unsubscribeToken = unsubscribeToken; }
}
