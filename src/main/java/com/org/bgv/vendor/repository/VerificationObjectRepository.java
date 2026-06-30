package com.org.bgv.vendor.repository;


import com.org.bgv.vendor.entity.VerificationObject;

import jakarta.transaction.Transactional;

import com.org.bgv.enums.VerificationObjectStatus;
import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.entity.VerificationCaseCheck;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationObjectRepository extends JpaRepository<VerificationObject, Long> {
    
    // Find by case check ID
    List<VerificationObject> findByVerificationCheck_CaseCheckId(Long caseCheckId);
    
    // Find by object type
    List<VerificationObject> findByObjectType(CheckCategoryEnum objectType);
    
    // Find by status
    List<VerificationObject> findByStatus(VerificationObjectStatus status);
    
    // Find by final result
    List<VerificationObject> findByFinalResult(VerificationObjectStatus finalResult);
    
    // Find by source ID
    Optional<VerificationObject> findBySourceId(Long sourceId);
    
    Optional<VerificationObject> findBySourceIdAndVerificationCheck_CaseCheckId(Long sourceId,Long caseCheckId);
    
    // Find by candidate submitted flag
    List<VerificationObject> findByCandidateSubmitted(Boolean candidateSubmitted);
    
    // Find by verified by user
    List<VerificationObject> findByVerifiedBy(Long verifiedBy);
    
    // Find by case check ID and object type
    List<VerificationObject> findByVerificationCheck_CaseCheckIdAndObjectType(Long caseCheckId, CheckCategoryEnum objectType);
    
    List<VerificationObject> findByVerificationCheckAndObjectType(VerificationCaseCheck check, CheckCategoryEnum objectType);
    
    // Find by case check ID and status
    List<VerificationObject> findByVerificationCheck_CaseCheckIdAndStatus(Long caseCheckId, VerificationObjectStatus status);
    
    // Find by case check ID and final result
    List<VerificationObject> findByVerificationCheck_CaseCheckIdAndFinalResult(Long caseCheckId, VerificationObjectStatus finalResult);
    
    // Find unverified objects (where verifiedAt is null and status is not COMPLETED)
    @Query("SELECT v FROM VerificationObject v WHERE v.verifiedAt IS NULL AND v.status != 'COMPLETED'")
    List<VerificationObject> findUnverifiedObjects();
    
    // Find objects verified after a specific date
    List<VerificationObject> findByVerifiedAtAfter(LocalDateTime date);
    
    // Find objects verified before a specific date
    List<VerificationObject> findByVerifiedAtBefore(LocalDateTime date);
    
    // Count by status
    long countByStatus(VerificationObjectStatus status);
    
    // Count by final result
    long countByFinalResult(VerificationObjectStatus finalResult);
    
    // Count by case check ID
    long countByVerificationCheck_CaseCheckId(Long caseCheckId);
    
    // Delete by case check ID
    void deleteByVerificationCheck_CaseCheckId(Long caseCheckId);
    
    // Check if exists by source ID and object type
    boolean existsBySourceIdAndObjectType(Long sourceId, CheckCategoryEnum objectType);
    
    // Find by object name containing (case insensitive)
    List<VerificationObject> findByObjectNameContainingIgnoreCase(String objectName);
    
    // Find by verification check case check ID with pagination
    // (For pagination, add Pageable parameter: Page<VerificationObject> findByVerificationCheck_CaseCheckId(Long caseCheckId, Pageable pageable)
    
    @Modifying
    @Transactional
    void deleteByVerificationCheckVerificationCaseCaseId(Long caseId);
}
