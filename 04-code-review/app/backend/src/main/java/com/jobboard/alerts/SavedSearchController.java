package com.jobboard.alerts;

import com.jobboard.security.JobSeekerPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/saved-searches")
public class SavedSearchController {

    private final SavedSearchRepository savedSearchRepository;

    public SavedSearchController(SavedSearchRepository savedSearchRepository) {
        this.savedSearchRepository = savedSearchRepository;
    }

    @PostMapping
    public SavedSearch create(@RequestBody SavedSearch search,
                              @AuthenticationPrincipal JobSeekerPrincipal user) {
        search.setUserId(user.id());
        search.setUnsubscribeToken(UnsubscribeTokens.generate(user.id(), search.getKeyword()));
        return savedSearchRepository.save(search);
    }

    @GetMapping
    public List<SavedSearch> mine(@AuthenticationPrincipal JobSeekerPrincipal user) {
        return savedSearchRepository.findByUserId(user.id());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id,
                                       @AuthenticationPrincipal JobSeekerPrincipal user) {
        savedSearchRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Linked from alert emails, so no login required
    @GetMapping("/unsubscribe")
    public ResponseEntity<Void> unsubscribe(@RequestParam String token,
                                            @RequestParam(defaultValue = "/") String next) {
        savedSearchRepository.findByUnsubscribeToken(token).ifPresent(savedSearchRepository::delete);
        return ResponseEntity.status(302).location(URI.create(next)).build();
    }
}
