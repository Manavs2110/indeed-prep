package com.jobboard.savedjob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/saved-jobs")
public class SavedJobController {

    @Autowired
    private SavedJobService savedJobService;

    @PostMapping("/{jobId}")
    public ResponseEntity<String> saveJob(@PathVariable String jobId,
                                          @RequestParam String userId) {
        try {
            savedJobService.saveJob(userId, jobId);
            return ResponseEntity.ok("saved");
        } catch (Exception e) {
            return ResponseEntity.ok("error: " + e.getMessage());
        }
    }

    @GetMapping
    public List<SavedJobDto> getSavedJobs(@RequestParam String userId) {
        return savedJobService.getSavedJobs(userId);
    }

    @DeleteMapping("/{jobId}")
    public void unsave(@PathVariable String jobId, @RequestParam String userId) {
        savedJobService.unsave(userId, jobId);
    }
}
