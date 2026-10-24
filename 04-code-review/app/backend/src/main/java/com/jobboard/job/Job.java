package com.jobboard.job;

import jakarta.persistence.*;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    private String id;

    private String title;
    private String company;
    private String location;
    private Integer salary;          // nullable: many employers don't post salary

    @Column(columnDefinition = "TEXT")
    private String description;      // employer-provided HTML, sanitized on render by JobDetailsPage

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getCompany() { return company; }
    public String getLocation() { return location; }
    public Integer getSalary() { return salary; }
    public String getDescription() { return description; }
    public JobStatus getStatus() { return status; }
}
