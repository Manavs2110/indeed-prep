package com.jobboard.security;

/** Populated by the auth filter from the session cookie on every request. */
public record JobSeekerPrincipal(String id, String email) {}
