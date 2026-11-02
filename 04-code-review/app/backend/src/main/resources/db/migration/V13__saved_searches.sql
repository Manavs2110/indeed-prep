ALTER TABLE jobs ADD COLUMN posted_at TIMESTAMP NOT NULL;

CREATE TABLE saved_searches (
    id                BIGSERIAL PRIMARY KEY,
    user_id           VARCHAR(64) NOT NULL,
    keyword           VARCHAR(255),
    location          VARCHAR(255),
    min_salary        INTEGER,
    frequency         INTEGER,
    last_sent_at      TIMESTAMP,
    unsubscribe_token VARCHAR(255)
);
