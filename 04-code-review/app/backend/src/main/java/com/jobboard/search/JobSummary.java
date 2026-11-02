package com.jobboard.search;

import java.util.Date;

public record JobSummary(String id, String title, String company, String location,
                         Integer salary, Date postedAt) {}
