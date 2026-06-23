package com.org.bgv.vendor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.vendor.dto.EvidenceSource;
import com.org.bgv.vendor.entity.VerificationActionEvidence;
import com.org.bgv.entity.VerificationCaseCheck;


@Repository
public interface VerificationActionEvidenceRepository
        extends JpaRepository<VerificationActionEvidence, Long> {

    List<VerificationActionEvidence> findByAction_Id(Long actionId);
    
    List<VerificationActionEvidence> findByVerificationCaseCheck(VerificationCaseCheck verificationCaseCheck);
    
    List<VerificationActionEvidence> findByVerificationCaseCheck_CaseCheckId(Long checkId);

    Optional<VerificationActionEvidence> findById(Long id);

    boolean existsByAction_IdAndId(Long actionId, Long id);
    
    List<VerificationActionEvidence> findByVerificationCaseCheck_CaseCheckIdAndObjectId(Long caseCheckId, Long objectId);
   
    
    @Modifying
    @Query("DELETE FROM VerificationActionEvidence e WHERE e.action.verificationCase.caseId = :caseId")
    void deleteByActionVerificationCaseCaseId(@Param("caseId") Long caseId);
    
    
    
   
    
    // ==================== DOCUMENT LEVEL ====================
    List<VerificationActionEvidence> findByDocumentId(Long documentId);
    
    // ==================== SOURCE LEVEL ====================
    List<VerificationActionEvidence> findBySource(EvidenceSource source);
    
    // ==================== CHECK + ACTION ====================
    List<VerificationActionEvidence> findByVerificationCaseCheck_CaseCheckIdAndAction_Id(
        Long checkId, Long actionId);
    
    // ==================== CHECK + SOURCE ====================
    List<VerificationActionEvidence> findByVerificationCaseCheck_CaseCheckIdAndSource(
        Long checkId, EvidenceSource source);
    
    // ==================== ACTION + SOURCE ====================
    List<VerificationActionEvidence> findByAction_IdAndSource(
        Long actionId, EvidenceSource source);
    
    // ==================== UPLOADER LEVEL ====================
    List<VerificationActionEvidence> findByUploadedBy(Long userId);
    
    // ==================== CHECK + UPLOADER ====================
    List<VerificationActionEvidence> findByVerificationCaseCheck_CaseCheckIdAndUploadedBy(
        Long checkId, Long userId);
    
    // ==================== ACTIVE ONLY ====================
    List<VerificationActionEvidence> findByArchivedFalse();
    
    // ==================== CHECK + ACTIVE ====================
    @Query("SELECT vae FROM VerificationActionEvidence vae " +
           "WHERE vae.verificationCaseCheck.caseCheckId = :checkId " +
           "AND vae.archived = false")
    List<VerificationActionEvidence> findActiveByCheckId(@Param("checkId") Long checkId);
    
    // ==================== ACTION + ACTIVE ====================
    @Query("SELECT vae FROM VerificationActionEvidence vae " +
           "WHERE vae.action.id = :actionId " +
           "AND vae.archived = false")
    List<VerificationActionEvidence> findActiveByAction_Id(@Param("actionId") Long actionId);
}