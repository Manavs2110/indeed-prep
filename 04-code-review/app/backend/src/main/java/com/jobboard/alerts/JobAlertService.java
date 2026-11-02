package com.jobboard.alerts;

import com.jobboard.email.EmailClient;
import com.jobboard.search.JobSearchService;
import com.jobboard.search.JobSummary;
import com.jobboard.search.SearchQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class JobAlertService {

    private static final Logger log = LoggerFactory.getLogger(JobAlertService.class);

    private final SavedSearchRepository savedSearchRepository;
    private final JobSearchService jobSearchService;
    private final EmailClient emailClient;

    public JobAlertService(SavedSearchRepository savedSearchRepository,
                           JobSearchService jobSearchService,
                           EmailClient emailClient) {
        this.savedSearchRepository = savedSearchRepository;
        this.jobSearchService = jobSearchService;
        this.emailClient = emailClient;
    }

    /** Every morning at 8am: email each user the new jobs matching their saved searches. */
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void sendDailyAlerts() {
        List<SavedSearch> searches = savedSearchRepository.findAll();
        log.info("Sending job alerts for {} saved searches", searches.size());

        for (SavedSearch s : searches) {
            if (s.getFrequency() == AlertFrequency.WEEKLY
                    && LocalDate.now().getDayOfWeek() != DayOfWeek.MONDAY) {
                continue;
            }

            List<JobSummary> jobs = jobSearchService.search(
                    new SearchQuery(s.getKeyword(), s.getLocation(), "posted_at", 0, 50));

            List<JobSummary> newJobs = new ArrayList<>();
            for (JobSummary job : jobs) {
                if (job.postedAt().after(s.getLastSentAt())
                        && job.salary() >= s.getMinSalary()) {
                    newJobs.add(job);
                }
            }
            if (newJobs.isEmpty()) {
                continue;
            }

            emailClient.send(s.getUserId(), buildEmail(s, newJobs));
            s.setLastSentAt(new Date());
            savedSearchRepository.save(s);
        }
    }

    private String buildEmail(SavedSearch s, List<JobSummary> jobs) {
        StringBuilder html = new StringBuilder("<h2>New jobs for \"" + s.getKeyword() + "\"</h2><ul>");
        for (JobSummary j : jobs) {
            html.append("<li><a href=\"https://jobboard.com/jobs/").append(j.id()).append("\">")
                .append(j.title()).append(" at ").append(j.company())
                .append("</a></li>");
        }
        html.append("</ul><a href=\"http://jobboard.com/api/saved-searches/unsubscribe?token=")
            .append(s.getUnsubscribeToken())
            .append("\">Unsubscribe</a>");
        return html.toString();
    }
}
