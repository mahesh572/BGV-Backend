package com.org.bgv.candidate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.candidate.entity.EducationHistory;

import java.util.List;
import java.util.Optional;

@Repository
public interface EducationHistoryRepository extends JpaRepository<EducationHistory, Long> {
    List<EducationHistory> findByProfile_ProfileId(Long profileId);
    
    // Method 1: Using derived query method (Spring Data JPA will generate the query)
    @Transactional
    @Modifying
    void deleteByProfile_ProfileId(Long profileId);
    
    // Method 2: Using custom JPQL query (alternative approach)
    @Transactional
    @Modifying
    @Query("DELETE FROM EducationHistory e WHERE e.profile.profileId = :profileId")
    void deleteAllByProfileId(@Param("profileId") Long profileId);
    
    // Method 3: Using native SQL query (alternative approach)
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM education_history WHERE profile_id = :profileId", nativeQuery = true)
    void deleteByProfileIdNative(@Param("profileId") Long profileId);
    
    @Transactional
    @Modifying
    void deleteByProfile_ProfileIdAndId(Long profileId, Long id);
    
    Optional<EducationHistory> findByProfile_ProfileIdAndId(Long profileId, Long id);
    
    List<EducationHistory> findByCandidateId(Long candidateId);
    
    List<EducationHistory> findByCandidateIdAndVerificationCaseCaseId(Long candidateId,Long caseId);
    
    @Query("SELECT e FROM EducationHistory e WHERE e.candidateId = :candidateId ORDER BY e.toDate DESC, e.fromDate DESC")
    List<EducationHistory> findByCandidateIdOrderByDate(@Param("candidateId") Long candidateId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM EducationHistory e WHERE e.candidateId = :candidateId AND e.id = :id")
    void deleteByCandidateIdAndId(@Param("candidateId") Long candidateId, @Param("id") Long id);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM EducationHistory e WHERE e.candidateId = :candidateId")
    void deleteAllByCandidateId(@Param("candidateId") Long candidateId);
    
    Long countByCandidateId(Long candidateId);
    
    Optional<EducationHistory> findByCandidateIdAndId(Long candidateId, Long id);
    
    EducationHistory findByCandidateIdAndVerificationCaseCaseIdAndId(Long candidateId,Long caseId,Long id);
}