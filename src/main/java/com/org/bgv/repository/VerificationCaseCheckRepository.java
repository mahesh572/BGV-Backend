package com.org.bgv.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.entity.VerificationCaseCheck;

@Repository
public interface VerificationCaseCheckRepository
        extends JpaRepository<VerificationCaseCheck, Long> {

    /**
     * Get all checks for a verification case
     */
    List<VerificationCaseCheck> findByVerificationCase_CaseId(Long caseId);

    /**
     * Get all checks by case and status
     */
    List<VerificationCaseCheck> findByVerificationCase_CaseIdAndStatus(
            Long caseId,
            CaseCheckStatus status
    );

    /**
     * Get a specific check inside a case (e.g., EDUCATION)
     */
    Optional<VerificationCaseCheck> findByVerificationCase_CaseIdAndCategory_CategoryId(
            Long caseId,
            Long categoryId
    );

    /**
     * Get all checks of a category across cases (admin / vendor use)
     */
    List<VerificationCaseCheck> findByCategory_CategoryId(Long categoryId);

    /**
     * Count checks by status for dashboard
     */
    long countByStatus(CaseCheckStatus status);
    
    List<VerificationCaseCheck> findTop10ByVendorIdOrderByUpdatedAtDesc(Long vendorId);
}