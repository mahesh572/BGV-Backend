package com.org.bgv.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.Job;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
 
 // Find all jobs by company
 Page<Job> findByCompanyId(Long companyId, Pageable pageable);
 
 // Find active jobs by company
 Page<Job> findByCompanyIdAndStatus(Long companyId, String status, Pageable pageable);
 
 // Find jobs by status
 Page<Job> findByStatus(String status, Pageable pageable);
 
 // Find jobs that are expiring soon
 List<Job> findByApplicationDeadlineBeforeAndStatus(LocalDateTime date, String status);
 
 // Search jobs by title and description
 @Query("SELECT j FROM Job j WHERE " +
        "(LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
        "LOWER(j.description) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
        "j.status = :status")
 Page<Job> searchJobs(@Param("search") String search, 
                      @Param("status") String status, 
                      Pageable pageable);
 
 // Search jobs within a company
 @Query("SELECT j FROM Job j WHERE " +
        "j.company.id = :companyId AND " +
        "(LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
        "LOWER(j.description) LIKE LOWER(CONCAT('%', :search, '%')))")
 Page<Job> searchCompanyJobs(@Param("companyId") Long companyId, 
                            @Param("search") String search, 
                            Pageable pageable);
 
 // Count active jobs by company
 Long countByCompanyIdAndStatus(Long companyId, String status);
 
 // Find job by ID and company (for security)
 Optional<Job> findByIdAndCompanyId(Long id, Long companyId);
 
 // Check if job exists and belongs to company
 boolean existsByIdAndCompanyId(Long id, Long companyId);
 

 // Company jobs by employment type
 Page<Job> findByCompanyIdAndEmploymentType(Long companyId, String employmentType, Pageable pageable);

 // Company jobs by experience level
 Page<Job> findByCompanyIdAndExperienceLevel(Long companyId, String experienceLevel, Pageable pageable);

 

 // Combined search with multiple filters
 @Query("SELECT j FROM Job j WHERE " +
        "j.company.id = :companyId AND " +
        "(:status IS NULL OR j.status = :status) AND " +
        "(:employmentType IS NULL OR j.employmentType = :employmentType) AND " +
        "(:experienceLevel IS NULL OR j.experienceLevel = :experienceLevel) AND " +
        "(:search IS NULL OR " +
        " LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
        " LOWER(j.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
        " LOWER(j.department) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
        " EXISTS (SELECT s FROM j.skills s WHERE LOWER(s) LIKE LOWER(CONCAT('%', :search, '%'))))")
 Page<Job> findByCompanyIdWithFilters(@Param("companyId") Long companyId,
                                     @Param("status") String status,
                                     @Param("employmentType") String employmentType,
                                     @Param("experienceLevel") String experienceLevel,
                                     @Param("search") String search,
                                     Pageable pageable);


 // Find expiring jobs for a company
 @Query("SELECT j FROM Job j WHERE " +
        "j.company.id = :companyId AND " +
        "j.applicationDeadline BETWEEN :startDate AND :endDate AND " +
        "j.status = 'active'")
 List<Job> findExpiringJobsByCompanyId(@Param("companyId") Long companyId,
                                      @Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);


 // Count total jobs for a company
 Long countByCompanyId(Long companyId);

 // Find jobs by multiple statuses
 @Query("SELECT j FROM Job j WHERE j.company.id = :companyId AND j.status IN :statuses")
 Page<Job> findByCompanyIdAndStatusIn(@Param("companyId") Long companyId,
                                     @Param("statuses") List<String> statuses,
                                     Pageable pageable);

 // Find jobs by location (city/state/country)
 @Query("SELECT j FROM Job j WHERE " +
        "j.company.id = :companyId AND " +
        "(:city IS NULL OR LOWER(j.location.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
        "(:state IS NULL OR LOWER(j.location.state) LIKE LOWER(CONCAT('%', :state, '%'))) AND " +
        "(:country IS NULL OR LOWER(j.location.country) LIKE LOWER(CONCAT('%', :country, '%')))")
 Page<Job> findByCompanyIdAndLocation(@Param("companyId") Long companyId,
                                     @Param("city") String city,
                                     @Param("state") String state,
                                     @Param("country") String country,
                                     Pageable pageable);

 // Find remote jobs for a company
 Page<Job> findByCompanyIdAndLocationRemote(Long companyId, Boolean remote, Pageable pageable);

 // Public job search (active jobs only)
 @Query("SELECT j FROM Job j WHERE " +
        "j.status = 'active' AND " +
        "(LOWER(j.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
        "LOWER(j.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
        "LOWER(j.department) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
        "LOWER(j.location.city) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
        "LOWER(j.location.country) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
        "EXISTS (SELECT s FROM j.skills s WHERE LOWER(s) LIKE LOWER(CONCAT('%', :search, '%'))))")
 Page<Job> searchPublicJobs(@Param("search") String search, Pageable pageable);

 // Public jobs with filters
 @Query("SELECT j FROM Job j WHERE " +
        "j.status = 'active' AND " +
        "(:employmentType IS NULL OR j.employmentType = :employmentType) AND " +
        "(:experienceLevel IS NULL OR j.experienceLevel = :experienceLevel) AND " +
        "(:remote IS NULL OR j.location.remote = :remote) AND " +
        "(:country IS NULL OR LOWER(j.location.country) LIKE LOWER(CONCAT('%', :country, '%')))")
 Page<Job> findPublicJobsWithFilters(@Param("employmentType") String employmentType,
                                    @Param("experienceLevel") String experienceLevel,
                                    @Param("remote") Boolean remote,
                                    @Param("country") String country,
                                    Pageable pageable);

 // Get latest jobs for a company
 @Query("SELECT j FROM Job j WHERE j.company.id = :companyId ORDER BY j.createdAt DESC")
 List<Job> findTopByCompanyIdOrderByCreatedAtDesc(@Param("companyId") Long companyId, Pageable pageable);

 @Query("SELECT j FROM Job j WHERE j.company.id = :companyId AND j.createdBy.userId = :createdById")
 Page<Job> findByCompanyIdAndCreatedBy(@Param("companyId") Long companyId,
                                      @Param("createdById") Long createdById,
                                      Pageable pageable);
}
