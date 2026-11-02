package com.jobboard.search;

public record SearchQuery(String keyword, String location, String sort, Integer page, Integer size) {}
