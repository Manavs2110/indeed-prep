package com.jobboard.savedjob;

import java.util.Date;

public record SavedJobDto(String id, String title, String company, Integer salary, Date savedAt) {}
