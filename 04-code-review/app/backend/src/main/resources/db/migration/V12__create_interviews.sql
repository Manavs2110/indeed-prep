CREATE TABLE interviews (
    id               VARCHAR(36)  PRIMARY KEY,
    job_id           VARCHAR(36)  NOT NULL,
    employer_id      VARCHAR(36)  NOT NULL,
    candidate_id     VARCHAR(36)  NOT NULL,
    candidate_email  VARCHAR(255),
    start_time       TIMESTAMP    NOT NULL,
    duration_minutes INT          NOT NULL,
    status           VARCHAR(20)  NOT NULL,
    meeting_link     VARCHAR(255),
    notes            TEXT,
    reminder_sent    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP    NOT NULL
);
