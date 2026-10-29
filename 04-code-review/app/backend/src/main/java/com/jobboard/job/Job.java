package com.jobboard.job;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    private String id;

    private String employerId;
    private String title;
    private String company;
    private String location;
    private Integer salary;          // nullable: many employers don't post salary

    @Column(columnDefinition = "TEXT")
    private String description;      // employer-provided HTML, sanitized on render by JobDetailsPage

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    private Date postedAt;
    private String contactEmail;
    private boolean promoted;
    private double promotionPrice;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmployerId() { return employerId; }
    public void setEmployerId(String employerId) { this.employerId = employerId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCompany() { return company; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Integer getSalary() { return salary; }
    public void setSalary(Integer salary) { this.salary = salary; }
    public String getDescription() { return description; }
    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }
    public Date getPostedAt() { return postedAt; }
    public void setPostedAt(Date postedAt) { this.postedAt = postedAt; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public boolean isPromoted() { return promoted; }
    public void setPromoted(boolean promoted) { this.promoted = promoted; }
    public double getPromotionPrice() { return promotionPrice; }
    public void setPromotionPrice(double promotionPrice) { this.promotionPrice = promotionPrice; }
}
