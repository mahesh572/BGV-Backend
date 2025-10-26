package com.org.bgv.controller;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.entity.Job;
import com.org.bgv.recruitement.dto.JobPostDTO;
import com.org.bgv.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/company/jobs")
@RequiredArgsConstructor
public class JobController {

    private static final Logger log = LoggerFactory.getLogger(JobController.class);
    
    private final JobService jobService;

    @PostMapping
    public ResponseEntity<CustomApiResponse<JobPostDTO>> createJob(
            @Valid @RequestBody JobPostDTO jobDTO,
            @RequestParam("companyId") Long companyId,
            @RequestParam("userId") Long userId) {
        
        log.info("Creating new job for company: {}, user: {}, job title: {}", 
                companyId, userId, jobDTO.getTitle());
        
        try {
            JobPostDTO createdJob = jobService.createJob(jobDTO, companyId, userId);
            return ResponseEntity.ok(CustomApiResponse.success("Job created successfully", createdJob, HttpStatus.OK));
        } catch (IllegalArgumentException e) {
            log.warn("Validation error in job creation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            log.error("Unexpected error during job creation: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to create job: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping
    public ResponseEntity<CustomApiResponse<Map<String, Object>>> getCompanyJobs(
            @RequestParam("companyId") Long companyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String employmentType,
            @RequestParam(required = false) String experienceLevel) {

        log.info("Fetching jobs for company: {} - page: {}, size: {}, sort: {}, direction: {}, search: {}, status: {}, employmentType: {}, experienceLevel: {}",
                companyId, page, size, sortBy, sortDirection, search, status, employmentType, experienceLevel);

        try {
            Pageable pageable = PageRequest.of(page, size, 
                Sort.by(Sort.Direction.fromString(sortDirection), sortBy));
            
            Map<String, Object> response = jobService.getCompanyJobsWithFilters(
                companyId, pageable, search, status, employmentType, experienceLevel);
            
            return ResponseEntity.ok(CustomApiResponse.success("Jobs retrieved successfully", response, HttpStatus.OK));
        } catch (Exception e) {
            log.error("Error fetching jobs for company {}: {}", companyId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve jobs: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse<Job>> getJob(
            @PathVariable Long id,
            @AuthenticationPrincipal(expression = "companyId") Long companyId) {
        
        log.info("Fetching job with ID: {} for company: {}", id, companyId);
        
        try {
            Job job = jobService.getCompanyJobById(id, companyId);
            return ResponseEntity.ok(CustomApiResponse.success("Job retrieved successfully", job, HttpStatus.OK));
        } catch (RuntimeException e) {
            log.warn("Job not found with ID: {} for company: {}", id, companyId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            log.error("Error fetching job with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve job: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<CustomApiResponse<Job>> updateJobStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @AuthenticationPrincipal(expression = "companyId") Long companyId) {
        
        log.info("Updating job status - ID: {}, status: {}, company: {}", id, status, companyId);
        
        try {
            Job updatedJob = jobService.updateJobStatus(id, status, companyId);
            return ResponseEntity.ok(CustomApiResponse.success("Job status updated successfully", updatedJob, HttpStatus.OK));
        } catch (RuntimeException e) {
            log.warn("Job not found with ID: {} for company: {}", id, companyId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            log.error("Error updating job status for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to update job status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse<Map<String, String>>> deleteJob(
            @PathVariable Long id,
            @AuthenticationPrincipal(expression = "companyId") Long companyId) {
        
        log.info("Deleting job with ID: {} for company: {}", id, companyId);
        
        try {
            jobService.deleteJob(id, companyId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Job deleted successfully");
            return ResponseEntity.ok(CustomApiResponse.success("Job deleted successfully", response, HttpStatus.OK));
        } catch (RuntimeException e) {
            log.warn("Job not found with ID: {} for company: {}", id, companyId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            log.error("Error deleting job with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to delete job: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    /*
    // Job statistics for dashboard
    @GetMapping("/statistics")
    public ResponseEntity<CustomApiResponse<Map<String, Object>>> getJobStatistics(
            @RequestParam("companyId") Long companyId) {
        
        log.info("Fetching job statistics for company: {}", companyId);
        
        try {
            Map<String, Object> statistics = jobService.getCompanyJobStatistics(companyId);
            return ResponseEntity.ok(CustomApiResponse.success("Job statistics retrieved successfully", statistics, HttpStatus.OK));
        } catch (Exception e) {
            log.error("Error fetching job statistics for company {}: {}", companyId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve job statistics: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // Expiring jobs endpoint
    @GetMapping("/expiring")
    public ResponseEntity<CustomApiResponse<List<Job>>> getExpiringJobs(
            @RequestParam("companyId") Long companyId,
            @RequestParam(defaultValue = "7") int days) {
        
        log.info("Fetching expiring jobs for company: {} within {} days", companyId, days);
        
        try {
            List<Job> expiringJobs = jobService.getExpiringJobs(companyId, days);
            return ResponseEntity.ok(CustomApiResponse.success("Expiring jobs retrieved successfully", expiringJobs, HttpStatus.OK));
        } catch (Exception e) {
            log.error("Error fetching expiring jobs for company {}: {}", companyId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve expiring jobs: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
*/
    // Public job details endpoint
    @GetMapping("/public/{id}")
    public ResponseEntity<CustomApiResponse<Job>> getPublicJob(@PathVariable Long id) {
        
        log.info("Fetching public job with ID: {}", id);
        
        try {
            Job job = jobService.getJobById(id);
            // Only return active jobs to public
            if (!"active".equalsIgnoreCase(job.getStatus())) {
                log.warn("Job with ID {} is not active", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(CustomApiResponse.failure("Job not found or not active", HttpStatus.NOT_FOUND));
            }
            return ResponseEntity.ok(CustomApiResponse.success("Job retrieved successfully", job, HttpStatus.OK));
        } catch (RuntimeException e) {
            log.warn("Public job not found with ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            log.error("Error fetching public job with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve job: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    // Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<CustomApiResponse<String>> healthCheck() {
        try {
            return ResponseEntity.ok(CustomApiResponse.success(
                "Job Service is running", 
                "Service is healthy", 
                HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                        "Service health check failed", 
                        HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }
}