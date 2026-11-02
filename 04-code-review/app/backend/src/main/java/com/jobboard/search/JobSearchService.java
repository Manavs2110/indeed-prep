package com.jobboard.search;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobSearchService {

    private static final RowMapper<JobSummary> ROW_MAPPER = (rs, i) -> new JobSummary(
            rs.getString("id"),
            rs.getString("title"),
            rs.getString("company"),
            rs.getString("location"),
            (Integer) rs.getObject("salary"),
            rs.getTimestamp("posted_at"));

    private final JdbcTemplate jdbc;

    public JobSearchService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Search is our hottest endpoint, so cache results
    @Cacheable(value = "jobSearch", key = "#query.keyword()")
    public List<JobSummary> search(SearchQuery query) {
        String sort = query.sort() == null ? "posted_at" : query.sort();
        int page = query.page() == null ? 0 : query.page();
        int size = query.size() == null ? 20 : query.size();

        String sql = String.format(
                "SELECT id, title, company, location, salary, posted_at FROM jobs " +
                "WHERE status = 'OPEN' AND LOWER(title) LIKE ? AND location LIKE ? " +
                "ORDER BY %s DESC LIMIT %d OFFSET %d", sort, size, page * size);

        return jdbc.query(sql, ROW_MAPPER,
                "%" + query.keyword().toLowerCase() + "%",
                "%" + (query.location() == null ? "" : query.location()) + "%");
    }
}
