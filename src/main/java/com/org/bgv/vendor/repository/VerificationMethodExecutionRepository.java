package com.org.bgv.vendor.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.enums.VerificationExecutionStatus;
import com.org.bgv.vendor.entity.VerificationMethodExecution;

@Repository
public interface VerificationMethodExecutionRepository
        extends JpaRepository<VerificationMethodExecution, Long> {

    List<VerificationMethodExecution>
        findByVerificationCheckCaseCheckId(Long checkId);
    
    List<VerificationMethodExecution>
    findByVerificationCheckCaseCheckIdAndObjectIdOrderByInitiatedAtDesc(
            Long checkId,
            Long objectId
    );
    
    
    boolean existsByVerificationCheckCaseCheckIdAndObjectIdAndVerificationMethodMethodIdAndStatusIn(
            Long checkId,
            Long objectId,
            Long methodId,
            List<VerificationExecutionStatus> statuses);
    
    boolean existsByVerificationCheckCaseCheckIdAndObjectIdAndVerificationMethodMethodIdAndStatusNotIn(
            Long checkId,
            Long objectId,
            Long methodId,
            List<VerificationExecutionStatus> statuses);
    
    
    
 // ==================== ACTION LEVEL ====================
    List<VerificationMethodExecution> findByAction_Id(Long actionId);
    
    // ==================== CHECK LEVEL ====================
    List<VerificationMethodExecution> findByVerificationCheck_CaseCheckId(Long checkId);
    
    // ==================== OBJECT LEVEL ====================
    @Query("SELECT vme FROM VerificationMethodExecution vme " +
           "WHERE vme.objectType = :objectType AND vme.objectId = :objectId")
    List<VerificationMethodExecution> findByObjectTypeAndObjectId(
        @Param("objectType") String objectType,
        @Param("objectId") Long objectId);
    
    // ==================== CHECK + OBJECT ====================
    @Query("SELECT vme FROM VerificationMethodExecution vme " +
           "WHERE vme.verificationCheck.caseCheckId = :checkId " +
           "AND vme.objectType = :objectType " +
           "AND vme.objectId = :objectId")
    List<VerificationMethodExecution> findByVerificationCheck_CaseCheckIdAndObjectTypeAndObjectId(
        @Param("checkId") Long checkId,
        @Param("objectType") String objectType,
        @Param("objectId") Long objectId);
    
    // ==================== CHECK + ACTION ====================
    @Query("SELECT vme FROM VerificationMethodExecution vme " +
           "WHERE vme.verificationCheck.caseCheckId = :checkId " +
           "AND vme.action.id = :actionId")
    List<VerificationMethodExecution> findByVerificationCheck_CaseCheckIdAndAction_Id(
        @Param("checkId") Long checkId,
        @Param("actionId") Long actionId);
    
    // ==================== CHECK + OBJECT + STATUS ====================
    @Query("SELECT vme FROM VerificationMethodExecution vme " +
           "WHERE vme.verificationCheck.caseCheckId = :checkId " +
           "AND vme.objectType = :objectType " +
           "AND vme.objectId = :objectId " +
           "AND vme.status = :status")
    List<VerificationMethodExecution> findByCheckAndObjectAndStatus(
        @Param("checkId") Long checkId,
        @Param("objectType") String objectType,
        @Param("objectId") Long objectId,
        @Param("status") VerificationExecutionStatus status);
    
    // ==================== OBJECT + STATUS ====================
    @Query("SELECT vme FROM VerificationMethodExecution vme " +
           "WHERE vme.objectType = :objectType " +
           "AND vme.objectId = :objectId " +
           "AND vme.status = :status")
    List<VerificationMethodExecution> findByObjectAndStatus(
        @Param("objectType") String objectType,
        @Param("objectId") Long objectId,
        @Param("status") VerificationExecutionStatus status);

}
