package com.jobboard.interview;

/** Request body for scheduling or rescheduling an interview. */
public class ScheduleRequest {
    public String jobId;
    public String candidateId;
    public String candidateEmail;
    public String startTime;      // e.g. "2026-11-04T14:30"
    public String timezone;       // e.g. "America/New_York"
    public int durationMinutes;
    public String notes;
}
