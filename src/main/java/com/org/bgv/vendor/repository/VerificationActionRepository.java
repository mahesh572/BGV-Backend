package com.org.bgv.vendor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.vendor.dto.ActionLevel;
import com.org.bgv.vendor.dto.ActionStatus;
import com.org.bgv.vendor.dto.ActionType;
import com.org.bgv.vendor.entity.VerificationAction;

@Repository
public interface VerificationActionRepository
        extends JpaRepository<VerificationAction, Long> {

    /* ===============================
       BASIC LOOKUPS
       =============================== */

    List<VerificationAction> findByVerificationCaseCaseId(Long caseId);

    List<VerificationAction> findByVerificationCaseCaseIdAndVerificationCaseCheckCaseCheckId(Long caseId, Long checkId);

    Optional<VerificationAction> findByIdAndStatus(
            Long id,
            ActionStatus status
    );

    /* ===============================
       DUPLICATE PREVENTION
       =============================== */

    boolean existsByVerificationCaseCaseIdAndVerificationCaseCheckCaseCheckIdAndActionLevelAndActionTypeAndStatus(
            Long caseId,
            Long checkId,
            ActionLevel actionLevel,
            ActionType actionType,
            ActionStatus status
    );

    /* ===============================
       LEVEL-SPECIFIC LOOKUPS
       =============================== */
	/*
	 * // CHECK-level action Optional<VerificationAction>
	 * findTopByVerificationCaseCaseIdAndVerificationCaseCheckCaseCheckIdAndActionLevelAndStatusOrderByCreatedAtDesc(
	 * Long caseId, Long checkId, ActionLevel actionLevel, ActionStatus status );
	 * 
	 * // OBJECT-level action Optional<VerificationAction>
	 * findTopByVerificationCaseCaseIdAndVerificationCaseCheckCaseCheckIdAndObjectIdAndActionLevelAndStatusOrderByCreatedAtDesc(
	 * Long caseId, Long checkId, Long objectId, ActionLevel actionLevel,
	 * ActionStatus status );
	 */
	/*
	 * // DOCUMENT-level action Optional<VerificationAction>
	 * findTopByVerificationCaseCaseIdAndVerificationCaseCheckCaseCheckIdAndDocIdAndActionLevelAndStatusOrderByCreatedAtDesc(
	 * Long caseId, Long checkId, Long docId, ActionLevel actionLevel, ActionStatus
	 * status );
	 */

    /* ===============================
       HISTORY & AUDIT
       =============================== */

    List<VerificationAction> findByVerificationCaseCaseIdAndVerificationCaseCheckCaseCheckIdOrderByCreatedAtDesc(
            Long caseId,
            Long checkId
    );

  //  List<VerificationAction> findByCreatedBy(Long userId);
    
    boolean existsByVerificationCaseCheckCaseCheckIdAndStatus(
            Long caseCheckId,
            ActionStatus status
    );

    boolean existsByVerificationCaseCheckCaseCheckIdAndActionTypeAndStatus(
            Long caseCheckId,
            ActionType actionType,
            ActionStatus status
    );

}

