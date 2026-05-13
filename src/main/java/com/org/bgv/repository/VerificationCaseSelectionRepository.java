package com.org.bgv.repository;

import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseSelection;

import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.dto.CheckCategoryEnum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationCaseSelectionRepository 
        extends JpaRepository<VerificationCaseSelection, Long> {

    // 🔹 Find all selections for a case
    List<VerificationCaseSelection> findByVerificationCase_CaseId(Long caseId);

    // 🔹 Find by case + type
    List<VerificationCaseSelection> findByVerificationCase_CaseIdAndType(
            Long caseId, CheckCategoryEnum type);

    // 🔹 Find specific selection (important for avoiding duplicates)
    Optional<VerificationCaseSelection> findByVerificationCaseAndTypeAndReferenceId(
            VerificationCase verificationCase,
            CheckCategoryEnum type,
            Long referenceId
    );

    // 🔹 Check if selection exists
    boolean existsByVerificationCaseAndTypeAndReferenceId(
            VerificationCase verificationCase,
            CheckCategoryEnum type,
            Long referenceId
    );

    // 🔹 Find by status (for vendor dashboard)
    List<VerificationCaseSelection> findByVerificationCase_CaseIdAndStatus(
            Long caseId,
            CaseCheckStatus status
    );

    // 🔹 Count by status (useful for progress %)
    long countByVerificationCase_CaseIdAndStatus(
            Long caseId,
            CaseCheckStatus status
    );

    // 🔹 Delete selections by case (for cleanup/testing)
    void deleteByVerificationCase_CaseId(Long caseId);
}