package com.jobboard.alerts;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedSearchRepository extends JpaRepository<SavedSearch, Long> {

    List<SavedSearch> findByUserId(String userId);

    Optional<SavedSearch> findByUnsubscribeToken(String token);
}
