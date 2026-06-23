package com.org.bgv.vendor.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.vendor.entity.VerificationMethodExecutionEvidence;

@Repository
public interface VerificationMethodExecutionEvidenceRepository
        extends JpaRepository<VerificationMethodExecutionEvidence, Long> {
	
	List<VerificationMethodExecutionEvidence> findByVerificationCaseCheck_CaseCheckId(Long caseCheckId);

    List<VerificationMethodExecutionEvidence> findByMethodExecutionExecutionId(Long methodExecutionId);

    List<VerificationMethodExecutionEvidence> findByDocumentId(Long documentId);

    List<VerificationMethodExecutionEvidence> findByUploadedBy(Long userId);

    List<VerificationMethodExecutionEvidence> findByArchivedFalse();
    
 
    
    // ==================== EXECUTION LEVEL ====================
    List<VerificationMethodExecutionEvidence> findByMethodExecution_ExecutionId(Long executionId);
    
        
    // ==================== CHECK + EXECUTION ====================
    List<VerificationMethodExecutionEvidence> findByVerificationCaseCheck_CaseCheckIdAndMethodExecution_ExecutionId(
        Long checkId, Long executionId);
    
        
    // ==================== CHECK + UPLOADER ====================
    List<VerificationMethodExecutionEvidence> findByVerificationCaseCheck_CaseCheckIdAndUploadedBy(
        Long checkId, Long userId);
    
        
    // ==================== CHECK + ACTIVE ====================
    @Query("SELECT vmee FROM VerificationMethodExecutionEvidence vmee " +
           "WHERE vmee.verificationCaseCheck.caseCheckId = :checkId " +
           "AND (vmee.archived = false OR vmee.archived IS NULL)")
    List<VerificationMethodExecutionEvidence> findActiveByCheckId(@Param("checkId") Long checkId);
    
    // ==================== EXECUTION + ACTIVE ====================
    @Query("SELECT vmee FROM VerificationMethodExecutionEvidence vmee " +
           "WHERE vmee.methodExecution.executionId = :executionId " +
           "AND (vmee.archived = false OR vmee.archived IS NULL)")
    List<VerificationMethodExecutionEvidence> findActiveByExecutionId(@Param("executionId") Long executionId);
    
    // ==================== COUNT BY CHECK ====================
    @Query("SELECT COUNT(vmee) FROM VerificationMethodExecutionEvidence vmee " +
           "WHERE vmee.verificationCaseCheck.caseCheckId = :checkId " +
           "AND (vmee.archived = false OR vmee.archived IS NULL)")
    Long countActiveByCheckId(@Param("checkId") Long checkId);
    
}