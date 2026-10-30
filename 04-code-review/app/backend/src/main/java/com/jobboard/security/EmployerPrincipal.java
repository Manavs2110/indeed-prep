package com.jobboard.security;

/** Populated by the auth filter for employer accounts. employerId is the company the user belongs to. */
public record EmployerPrincipal(String userId, String employerId, String email) {}
