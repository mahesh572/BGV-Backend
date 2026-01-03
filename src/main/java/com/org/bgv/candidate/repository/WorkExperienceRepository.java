package com.org.bgv.candidate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.org.bgv.candidate.entity.EducationHistory;
import com.org.bgv.candidate.entity.WorkExperience;

import jakarta.transaction.Transactional;


public interface WorkExperienceRepository extends JpaRepository<WorkExperience, Long> {
	
	List<WorkExperience> findByProfile_ProfileId(Long profileId);
    void deleteByProfile_ProfileId(Long profileId); // Optional - if you want to replace all experiences
    List<WorkExperience> findByExperienceId(Long experienceId);
 // (Optional) Find a single work experience by profileId + experienceId for safe deletion
    Optional<WorkExperience> findByProfile_ProfileIdAndExperienceId(Long profileId, Long experienceId);
    
    // Verification
    
    List<WorkExperience> findByCandidateId(Long candidateId);
    
   
    Optional<WorkExperience> findByCandidateIdAndExperienceId(Long candidateId, Long experienceId);
    WorkExperience findByCandidateIdAndVerificationCaseCaseIdAndExperienceId(Long candidateId,Long caseId,Long experienceId);
    List<WorkExperience> findByCandidateIdAndVerificationCaseCaseId(Long candidateId,Long caseId);
    
    @Query("SELECT w FROM WorkExperience w WHERE w.candidateId = :candidateId " +
           "ORDER BY " +
           "CASE WHEN w.currentlyWorking = true THEN 0 ELSE 1 END, " +
           "COALESCE(w.end_date, CURRENT_DATE) DESC, " +
           "w.start_date DESC")
    List<WorkExperience> findByCandidateIdOrderByDate(@Param("candidateId") Long candidateId);
    
    List<WorkExperience> findByCandidateIdAndCurrentlyWorking(Long candidateId, boolean currentlyWorking);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM WorkExperience w WHERE w.candidateId = :candidateId AND w.id = :id")
    void deleteByCandidateIdAndId(@Param("candidateId") Long candidateId, @Param("id") Long id);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM WorkExperience w WHERE w.candidateId = :candidateId")
    void deleteAllByCandidateId(@Param("candidateId") Long candidateId);
    
    @Query("SELECT COUNT(w) FROM WorkExperience w WHERE w.candidateId = :candidateId")
    Long countByCandidateId(@Param("candidateId") Long candidateId);
    
    @Query("SELECT COUNT(w) FROM WorkExperience w WHERE w.candidateId = :candidateId AND w.verified = true")
    Long countVerifiedByCandidateId(@Param("candidateId") Long candidateId);
    
    
}
