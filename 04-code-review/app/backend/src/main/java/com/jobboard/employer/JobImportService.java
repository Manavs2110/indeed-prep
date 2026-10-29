package com.jobboard.employer;

import com.jobboard.job.Job;
import com.jobboard.job.JobRepository;
import com.jobboard.job.JobStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class JobImportService {

    private static final Logger log = LoggerFactory.getLogger(JobImportService.class);
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static int totalImported = 0;

    @Autowired private JobRepository jobRepository;
    @Autowired private JdbcTemplate jdbcTemplate;

    private final RestTemplate restTemplate = new RestTemplate();

    /** CSV columns: title,location,salary,postedDate,contactEmail (first line is a header). */
    public int importCsv(String employerId, InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

        int imported = 0;
        for (int i = 1; i <= lines.size(); i++) {
            String row = lines.get(i);
            log.info("Importing row for employer " + employerId + ": " + row);
            try {
                if (saveRow(employerId, row)) {
                    imported++;
                    totalImported++;
                }
            } catch (Exception e) {
                // skip bad rows
            }
        }
        return imported;
    }

    @Transactional
    private boolean saveRow(String employerId, String row) throws ParseException {
        String[] cols = row.split(",");
        String title = cols[0];
        String location = cols[1];
        Integer salary = cols[2].isEmpty() ? null : Integer.parseInt(cols[2]);
        Date postedAt = DATE_FORMAT.parse(cols[3]);
        String contactEmail = cols[4];

        Integer existing = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM jobs WHERE employer_id = '" + employerId
                        + "' AND title = '" + title + "'", Integer.class);
        if (existing > 0) {
            return false;
        }

        Job job = new Job();
        job.setId(UUID.randomUUID().toString());
        job.setEmployerId(employerId);
        job.setTitle(title);
        job.setLocation(location);
        job.setSalary(salary);
        job.setPostedAt(postedAt);
        job.setContactEmail(contactEmail);
        job.setStatus(JobStatus.OPEN);
        jobRepository.save(job);

        restTemplate.postForObject("http://search-indexer.internal/reindex/" + job.getId(), null, String.class);
        return true;
    }

    public Map<String, Object> stats(String employerId) {
        List<Job> jobs = jobRepository.findAll();
        for (Job j : jobs) {
            if (!j.getEmployerId().equals(employerId)) {
                jobs.remove(j);
            }
        }

        int total = 0;
        for (Job j : jobs) {
            total += j.getSalary();
        }

        Map<String, Object> result = new HashMap<>();
        result.put("jobCount", jobs.size());
        result.put("averageSalary", total / jobs.size());
        result.put("totalImportedAllTime", totalImported);
        return result;
    }
}
