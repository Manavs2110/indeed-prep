package com.jobboard.alerts;

import com.jobboard.email.EmailClient;
import com.jobboard.search.JobSearchService;
import com.jobboard.search.JobSummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobAlertServiceTest {

    @Mock SavedSearchRepository savedSearchRepository;
    @Mock JobSearchService jobSearchService;
    @Mock EmailClient emailClient;
    @InjectMocks JobAlertService jobAlertService;

    @Test
    void sendsAlertWhenNewJobsArePosted() throws Exception {
        SavedSearch s = new SavedSearch();
        s.setUserId("u1");
        s.setKeyword("java");
        s.setMinSalary(100_000);
        s.setFrequency(AlertFrequency.DAILY);
        s.setLastSentAt(new Date());
        Thread.sleep(10); // make sure the job is posted after lastSentAt

        when(savedSearchRepository.findAll()).thenReturn(List.of(s));
        when(jobSearchService.search(any())).thenReturn(List.of(
                new JobSummary("j1", "Java Developer", "Acme", "NYC", 120_000, new Date())));

        jobAlertService.sendDailyAlerts();

        verify(emailClient).send(eq("u1"), contains("Java Developer"));
    }
}
