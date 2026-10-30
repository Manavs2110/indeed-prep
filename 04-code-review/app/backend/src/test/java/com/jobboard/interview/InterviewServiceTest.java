package com.jobboard.interview;

import com.jobboard.email.EmailClient;
import com.jobboard.web.ValidationException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InterviewServiceTest {

    private final InterviewRepository repo = mock(InterviewRepository.class);
    private final EmailClient email = mock(EmailClient.class);
    private final InterviewService service = new InterviewService(repo, email);

    @Test
    void schedulesInterview() {
        when(repo.findByEmployerId("emp-1")).thenReturn(List.of());

        Interview result = service.schedule("emp-1", request("2026-11-04T14:30", 60));

        assertEquals("SCHEDULED", result.getStatus());
        verify(repo).save(any());
        verify(email).send(eq("cand-1"), any());
    }

    @Test
    void rejectsDoubleBooking() {
        Interview existing = new Interview();
        existing.setStartTime(LocalDateTime.parse("2026-11-04T14:30"));
        when(repo.findByEmployerId("emp-1")).thenReturn(List.of(existing));

        assertThrows(ValidationException.class,
                () -> service.schedule("emp-1", request("2026-11-04T14:30", 60)));
    }

    @Test
    void rejectsVeryLongInterviews() {
        assertThrows(ValidationException.class,
                () -> service.schedule("emp-1", request("2026-11-04T14:30", 300)));
    }

    private ScheduleRequest request(String start, int minutes) {
        ScheduleRequest req = new ScheduleRequest();
        req.jobId = "job-1";
        req.candidateId = "cand-1";
        req.candidateEmail = "sam@example.com";
        req.startTime = start;
        req.timezone = "America/New_York";
        req.durationMinutes = minutes;
        return req;
    }
}
