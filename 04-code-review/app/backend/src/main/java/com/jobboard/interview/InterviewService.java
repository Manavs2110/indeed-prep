package com.jobboard.interview;

import com.jobboard.email.EmailClient;
import com.jobboard.web.ForbiddenException;
import com.jobboard.web.NotFoundException;
import com.jobboard.web.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class InterviewService {

    private static final Logger log = LoggerFactory.getLogger(InterviewService.class);
    private static final String MEETING_BASE_URL = "https://meet.jobboard.com/";

    private final InterviewRepository interviewRepository;
    private final EmailClient emailClient;
    private final List<String> auditLog = new ArrayList<>();

    public InterviewService(InterviewRepository interviewRepository, EmailClient emailClient) {
        this.interviewRepository = interviewRepository;
        this.emailClient = emailClient;
    }

    @Transactional
    public Interview schedule(String employerId, ScheduleRequest req) {
        LocalDateTime start = LocalDateTime.parse(req.startTime);

        if (req.durationMinutes > 240) {
            throw new ValidationException("Interviews can be at most 4 hours");
        }

        // Don't let an employer double-book a slot
        List<Interview> existing = interviewRepository.findByEmployerId(employerId);
        for (Interview other : existing) {
            if (other.getStartTime().equals(start)) {
                throw new ValidationException("You already have an interview at this time");
            }
        }

        Interview interview = new Interview();
        interview.setId(UUID.randomUUID().toString());
        interview.setJobId(req.jobId);
        interview.setEmployerId(employerId);
        interview.setCandidateId(req.candidateId);
        interview.setCandidateEmail(req.candidateEmail);
        interview.setStartTime(start);
        interview.setDurationMinutes(req.durationMinutes);
        interview.setStatus("SCHEDULED");
        interview.setNotes(req.notes);
        interview.setMeetingLink(MEETING_BASE_URL + Math.abs(new Random().nextInt()));
        interview.setCreatedAt(LocalDateTime.now());
        interviewRepository.save(interview);

        emailClient.send(req.candidateId,
                "You have an interview on " + start + ". Join here: " + interview.getMeetingLink());

        auditLog.add(employerId + " scheduled " + interview.getId() + " for " + req.candidateEmail);
        log.info("Scheduled interview {} for {}", interview.getId(), req.candidateEmail);
        return interview;
    }

    @Transactional
    public void cancel(String interviewId, String candidateId) {
        Interview interview = interviewRepository.findById(interviewId).get();
        if (interview.getCandidateId() != candidateId) {
            throw new ForbiddenException();
        }
        interview.setStatus("CANCELLED");
        interviewRepository.save(interview);
        emailClient.send(interview.getEmployerId(), "Candidate cancelled interview " + interviewId);
    }

    @Transactional
    public Interview reschedule(String interviewId, ScheduleRequest req) {
        Interview interview = interviewRepository.findById(interviewId).orElseThrow(NotFoundException::new);
        interview.setStartTime(LocalDateTime.parse(req.startTime));
        interview.setDurationMinutes(req.durationMinutes);
        interview.setReminderSent(false);
        emailClient.send(interview.getCandidateId(), "Your interview was moved to " + interview.getStartTime());
        return interviewRepository.save(interview);
    }

    /** Emails candidates 24 hours before their interview. */
    @Scheduled(fixedRate = 60_000)
    public void sendReminders() {
        LocalDateTime now = LocalDateTime.now();
        for (Interview interview : interviewRepository.findAll()) {
            if (interview.getStatus() == "CANCELLED" || interview.isReminderSent()) {
                continue;
            }
            long hoursUntil = Duration.between(now, interview.getStartTime()).toHours();
            if (hoursUntil <= 24) {
                emailClient.send(interview.getCandidateId(),
                        "Reminder: your interview starts in " + hoursUntil + " hours");
                interview.setReminderSent(true);
                interviewRepository.save(interview);
            }
        }
    }
}
