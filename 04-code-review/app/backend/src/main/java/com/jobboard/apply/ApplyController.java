package com.jobboard.apply;

import com.jobboard.security.JobSeekerPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Existing endpoint — shows the team's conventions for auth and errors. */
@RestController
@RequestMapping("/api/applications")
public class ApplyController {

    private final ApplyService applyService;

    public ApplyController(ApplyService applyService) {
        this.applyService = applyService;
    }

    @PostMapping("/{jobId}")
    public ResponseEntity<Void> apply(@PathVariable String jobId,
                                      @AuthenticationPrincipal JobSeekerPrincipal user) {
        applyService.apply(user.id(), jobId);   // throws JobNotFoundException / JobClosedException,
        return ResponseEntity.status(201).build(); // mapped to 404 / 409 by ApiExceptionHandler
    }
}
