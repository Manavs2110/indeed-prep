package com.jobboard.search;

/**
 * Existing client for the search indexer. Reindex requests go onto a queue and are
 * processed asynchronously with retries, so callers never block on the indexer.
 */
public interface SearchIndexClient {
    void enqueueReindex(String jobId);
}
