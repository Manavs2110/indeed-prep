package com.jobboard.employer;

import com.jobboard.job.Job;
import com.jobboard.job.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/employers/{employerId}/jobs")
public class JobImportController {

    @Autowired
    private JobImportService importService;

    @Autowired
    private PromotionPricingService pricingService;

    @Autowired
    private JobRepository jobRepository;

    @PostMapping("/import")
    public ResponseEntity<String> importJobs(@PathVariable String employerId,
                                             @RequestParam("file") MultipartFile file) {
        try {
            int count = importService.importCsv(employerId, file.getInputStream());
            return ResponseEntity.ok("Imported " + count + " jobs");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok("Import failed: " + e.toString());
        }
    }

    @GetMapping("/stats")
    public Map<String, Object> stats(@PathVariable String employerId) {
        return importService.stats(employerId);
    }

    @PostMapping("/{jobId}/promote")
    public Job promote(@PathVariable String employerId,
                       @PathVariable String jobId,
                       @RequestParam int days,
                       @RequestBody Job job) {
        double price = pricingService.price(employerId, days);
        job.setPromoted(true);
        job.setPromotionPrice(price);
        return jobRepository.save(job);
    }
}
