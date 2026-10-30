package com.jobboard.interview;

import com.jobboard.security.EmployerPrincipal;
import com.jobboard.security.JobSeekerPrincipal;
import com.jobboard.web.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interviews")
public class InterviewController {

    private final InterviewService interviewService;
    private final InterviewRepository interviewRepository;

    public InterviewController(InterviewService interviewService, InterviewRepository interviewRepository) {
        this.interviewService = interviewService;
        this.interviewRepository = interviewRepository;
    }

    /** Employer schedules an interview with a candidate. */
    @PostMapping
    public ResponseEntity<Interview> schedule(@RequestBody ScheduleRequest request,
                                              @AuthenticationPrincipal EmployerPrincipal employer) {
        Interview interview = interviewService.schedule(employer.employerId(), request);
        return ResponseEntity.status(201).body(interview);
    }

    @GetMapping("/{id}")
    public Interview get(@PathVariable String id) {
        return interviewRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    /** Candidate's "My interviews" page. */
    @GetMapping("/mine")
    public List<Interview> mine(@RequestParam String candidateId) {
        return interviewRepository.findByCandidateId(candidateId);
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<String> cancel(@PathVariable String id,
                                         @AuthenticationPrincipal JobSeekerPrincipal user) {
        try {
            interviewService.cancel(id, user.id());
            return ResponseEntity.ok("Cancelled");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/reschedule")
    public Interview reschedule(@PathVariable String id,
                                @RequestBody ScheduleRequest request,
                                @AuthenticationPrincipal EmployerPrincipal employer) {
        return interviewService.reschedule(id, request);
    }
}
