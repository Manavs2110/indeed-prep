package com.jobboard.email;

/** Thin wrapper over the transactional email provider. Calls are synchronous (~300-800 ms). */
public interface EmailClient {
    void send(String userId, String message);
}
