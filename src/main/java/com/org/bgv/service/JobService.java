package com.org.bgv.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.entity.Company;
import com.org.bgv.entity.Job;
import com.org.bgv.entity.User;
import com.org.bgv.mapper.JobMapper;
import com.org.bgv.recruitement.dto.JobPostDTO;
import com.org.bgv.repository.CompanyRepository;
import com.org.bgv.repository.JobRepository;
import com.org.bgv.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

 private final JobRepository jobRepository;
 private final UserRepository userRepository;
 private final CompanyRepository companyRepository;
 private final JobMapper jobMapper;

 
 @Transactional
 public JobPostDTO createJob(JobPostDTO jobDTO, Long companyId, Long userId) {
     log.info("Creating new job for company: {}, user: {}", companyId, userId);
     
     Job job=jobMapper.toEntity(jobDTO);
     
     
     // Validate company exists
     Company company = companyRepository.findById(companyId)
             .orElseThrow(() -> new RuntimeException("Company not found with id: " + companyId));
     
     // Validate user exists and belongs to company
   
     /*
     User user = userRepository.findByIdAndCompanyId(userId, companyId)
             .orElseThrow(() -> new RuntimeException("User not found or doesn't belong to company"));
     */
     
     User user = userRepository.findById(userId)
             .orElseThrow(() -> new RuntimeException("User not found: " + userId));
     
     // Set company and created by
     job.setCompany(company);
     job.setCreatedBy(user);
     
     // Set default status if not provided
     if (job.getStatus() == null) {
         job.setStatus("ACTIVE");
     }
     
     // Validate salary range
     if (job.getSalary() != null && job.getSalary().getMin() != null && 
         job.getSalary().getMax() != null) {
         if (job.getSalary().getMin() > job.getSalary().getMax()) {
             throw new RuntimeException("Minimum salary cannot be greater than maximum salary");
         }
     }
     
     // Validate application deadline
     if (job.getApplicationDeadline() != null && 
         job.getApplicationDeadline().isBefore(LocalDateTime.now())) {
         throw new RuntimeException("Application deadline cannot be in the past");
     }
     
     Job savedJob = jobRepository.save(job);
     jobDTO = jobMapper.toDTO(savedJob);
     log.info("Job created successfully with id: {}", savedJob.getId());
     return jobDTO;
 }

 @Transactional(readOnly = true)
 public Map<String, Object> getCompanyJobsWithFilters(
         Long companyId, 
         Pageable pageable, 
         String search, 
         String status, 
         String employmentType, 
         String experienceLevel) {
     
     Page<Job> jobsPage;
     
     if (search != null && !search.trim().isEmpty()) {
         jobsPage = jobRepository.searchCompanyJobs(companyId, search.trim(), pageable);
     } else if (status != null) {
         jobsPage = jobRepository.findByCompanyIdAndStatus(companyId, status, pageable);
     } else if (employmentType != null) {
         jobsPage = jobRepository.findByCompanyIdAndEmploymentType(companyId, employmentType, pageable);
     } else if (experienceLevel != null) {
         jobsPage = jobRepository.findByCompanyIdAndExperienceLevel(companyId, experienceLevel, pageable);
     } else {
         jobsPage = jobRepository.findByCompanyId(companyId, pageable);
     }

     // Convert to JobDTO
     List<JobPostDTO> jobDTOs = jobMapper.toDTOList(jobsPage.getContent());
     
    

     Map<String, Object> response = new HashMap<>();
     response.put("jobs", jobDTOs);
     response.put("currentPage", jobsPage.getNumber());
     response.put("totalItems", jobsPage.getTotalElements());
     response.put("totalPages", jobsPage.getTotalPages());
     response.put("pageSize", jobsPage.getSize());
     response.put("hasNext", jobsPage.hasNext());
     response.put("hasPrevious", jobsPage.hasPrevious());
     
     return response;
 }

 // Add these repository methods if needed
 public Page<Job> findByCompanyIdAndEmploymentType(Long companyId, String employmentType, Pageable pageable) {
     return jobRepository.findByCompanyIdAndEmploymentType(companyId, employmentType, pageable);
 }

 public Page<Job> findByCompanyIdAndExperienceLevel(Long companyId, String experienceLevel, Pageable pageable) {
     return jobRepository.findByCompanyIdAndExperienceLevel(companyId, experienceLevel, pageable);
 }
 /*
 // Update getExpiringJobs to accept companyId
 public List<Job> getExpiringJobs(Long companyId, int days) {
     // Implement logic to get expiring jobs for specific company
     return jobRepository.findExpiringJobsByCompanyId(companyId, days);
 }
 */
 @Transactional(readOnly = true)
 public Job getJobById(Long id) {
     log.debug("Fetching job by id: {}", id);
     return jobRepository.findById(id)
             .orElseThrow(() -> new RuntimeException("Job not found with id: " + id));
 }

 
 @Transactional(readOnly = true)
 public Job getCompanyJobById(Long id, Long companyId) {
     log.debug("Fetching job by id: {} for company: {}", id, companyId);
     return jobRepository.findByIdAndCompanyId(id, companyId)
             .orElseThrow(() -> new RuntimeException("Job not found or doesn't belong to company"));
 }


 @Transactional(readOnly = true)
 public Page<Job> getAllJobs(Pageable pageable) {
     log.debug("Fetching all jobs with pagination");
     return jobRepository.findAll(pageable);
 }

 
 @Transactional(readOnly = true)
 public Page<Job> getCompanyJobs(Long companyId, Pageable pageable) {
     log.debug("Fetching jobs for company: {}", companyId);
     return jobRepository.findByCompanyId(companyId, pageable);
 }

 
 @Transactional(readOnly = true)
 public Page<Job> getJobsByStatus(String status, Pageable pageable) {
     log.debug("Fetching jobs by status: {}", status);
     return jobRepository.findByStatus(status, pageable);
 }

 
 @Transactional(readOnly = true)
 public Page<Job> getCompanyJobsByStatus(Long companyId, String status, Pageable pageable) {
     log.debug("Fetching jobs for company: {} with status: {}", companyId, status);
     return jobRepository.findByCompanyIdAndStatus(companyId, status, pageable);
 }

 
 @Transactional(readOnly = true)
 public Page<Job> searchJobs(String search, String status, Pageable pageable) {
     log.debug("Searching jobs with query: {} and status: {}", search, status);
     return jobRepository.searchJobs(search, status, pageable);
 }

 
 @Transactional(readOnly = true)
 public Page<Job> searchCompanyJobs(Long companyId, String search, Pageable pageable) {
     log.debug("Searching company jobs for company: {} with query: {}", companyId, search);
     return jobRepository.searchCompanyJobs(companyId, search, pageable);
 }

 
 @Transactional
 public Job updateJob(Long id, Job jobDetails, Long companyId) {
     log.info("Updating job with id: {} for company: {}", id, companyId);
     
     Job existingJob = getCompanyJobById(id, companyId);
     
     // Update fields
     if (jobDetails.getTitle() != null) {
         existingJob.setTitle(jobDetails.getTitle());
     }
     if (jobDetails.getDepartment() != null) {
         existingJob.setDepartment(jobDetails.getDepartment());
     }
     if (jobDetails.getDescription() != null) {
         existingJob.setDescription(jobDetails.getDescription());
     }
     if (jobDetails.getRequirements() != null) {
         existingJob.setRequirements(jobDetails.getRequirements());
     }
     if (jobDetails.getResponsibilities() != null) {
         existingJob.setResponsibilities(jobDetails.getResponsibilities());
     }
     if (jobDetails.getSkills() != null) {
         existingJob.setSkills(jobDetails.getSkills());
     }
     if (jobDetails.getLocation() != null) {
         existingJob.setLocation(jobDetails.getLocation());
     }
     if (jobDetails.getSalary() != null) {
         existingJob.setSalary(jobDetails.getSalary());
     }
     if (jobDetails.getEmploymentType() != null) {
         existingJob.setEmploymentType(jobDetails.getEmploymentType());
     }
     if (jobDetails.getExperienceLevel() != null) {
         existingJob.setExperienceLevel(jobDetails.getExperienceLevel());
     }
     if (jobDetails.getEducationLevel() != null) {
         existingJob.setEducationLevel(jobDetails.getEducationLevel());
     }
     if (jobDetails.getVacancies() != null) {
         existingJob.setVacancies(jobDetails.getVacancies());
     }
     if (jobDetails.getApplicationDeadline() != null) {
         existingJob.setApplicationDeadline(jobDetails.getApplicationDeadline());
     }
     if (jobDetails.getStatus() != null) {
         existingJob.setStatus(jobDetails.getStatus());
     }
     
     // Validate salary range
     if (existingJob.getSalary() != null && existingJob.getSalary().getMin() != null && 
         existingJob.getSalary().getMax() != null) {
         if (existingJob.getSalary().getMin() > existingJob.getSalary().getMax()) {
             throw new RuntimeException("Minimum salary cannot be greater than maximum salary");
         }
     }
     
     Job updatedJob = jobRepository.save(existingJob);
     log.info("Job updated successfully with id: {}", updatedJob.getId());
     return updatedJob;
 }

 
 @Transactional
 public Job updateJobStatus(Long id, String status, Long companyId) {
     log.info("Updating job status to: {} for job id: {} and company: {}", status, id, companyId);
     
     Job existingJob = getCompanyJobById(id, companyId);
     existingJob.setStatus(status);
     
     Job updatedJob = jobRepository.save(existingJob);
     log.info("Job status updated successfully for id: {}", updatedJob.getId());
     return updatedJob;
 }

 
 @Transactional
 public void deleteJob(Long id, Long companyId) {
     log.info("Deleting job with id: {} for company: {}", id, companyId);
     
     Job job = getCompanyJobById(id, companyId);
     
     // Check if there are applications for this job
     /*
     if (!job.getApplications().isEmpty()) {
         throw new RuntimeException("Cannot delete job with existing applications");
     }
     */
     jobRepository.delete(job);
     log.info("Job deleted successfully with id: {}", id);
 }

 
 @Transactional(readOnly = true)
 public List<Job> getExpiringJobs(int days) {
     log.debug("Fetching jobs expiring within {} days", days);
     LocalDateTime deadline = LocalDateTime.now().plusDays(days);
     return jobRepository.findByApplicationDeadlineBeforeAndStatus(deadline, "ACTIVE");
 }
/*
 
 @Transactional(readOnly = true)
 public JobStatistics getCompanyJobStatistics(Long companyId) {
     log.debug("Fetching job statistics for company: {}", companyId);
     
     return new JobStatistics() {
         @Override
         public Long getTotalJobs() {
             return jobRepository.countByCompanyIdAndStatus(companyId, null);
         }

         @Override
         public Long getActiveJobs() {
             return jobRepository.countByCompanyIdAndStatus(companyId, Job.JobStatus.ACTIVE);
         }

         @Override
         public Long getClosedJobs() {
             return jobRepository.countByCompanyIdAndStatus(companyId, Job.JobStatus.CLOSED);
         }

         @Override
         public Long getTotalApplications() {
             // This would require a join with applications table
             // For now, return 0 - you can implement this based on your application structure
             return 0L;
         }

         @Override
         public Long getPendingApplications() {
             // This would require a join with applications table
             // For now, return 0 - you can implement this based on your application structure
             return 0L;
         }
     };
 }
 */
}
