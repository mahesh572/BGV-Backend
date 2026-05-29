package com.org.bgv.repository;

import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseSelection;

import jakarta.transaction.Transactional;

import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.dto.CheckCategoryEnum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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
    
    
    
 
    
    // Find all selections for a case
    List<VerificationCaseSelection> findByVerificationCase(VerificationCase verificationCase);
    
    // Find all selections for a case by type
    List<VerificationCaseSelection> findByVerificationCaseAndType(VerificationCase verificationCase, CheckCategoryEnum type);
    
    // Find base selections (included in package)
    List<VerificationCaseSelection> findByVerificationCaseAndIncludedInBaseTrue(VerificationCase verificationCase);
    
    // Find add-on selections (not included in base)
    List<VerificationCaseSelection> findByVerificationCaseAndIncludedInBaseFalse(VerificationCase verificationCase);
    
    // Find selections with price > 0 (paid add-ons)
    @Query("SELECT vcs FROM VerificationCaseSelection vcs WHERE vcs.verificationCase = :caseObj " +
           "AND vcs.includedInBase = false AND vcs.unitPrice > 0")
    List<VerificationCaseSelection> findPaidAddonsByVerificationCase(@Param("caseObj") VerificationCase verificationCase);
    
    // Calculate total add-on amount for a case
    @Query("SELECT COALESCE(SUM(vcs.unitPrice), 0) FROM VerificationCaseSelection vcs " +
           "WHERE vcs.verificationCase = :caseObj AND vcs.includedInBase = false")
    BigDecimal calculateTotalAddonAmount(@Param("caseObj") VerificationCase verificationCase);
    
    // Count selections by type and included status
    @Query("SELECT vcs.type, vcs.includedInBase, COUNT(vcs) FROM VerificationCaseSelection vcs " +
           "WHERE vcs.verificationCase = :caseObj GROUP BY vcs.type, vcs.includedInBase")
    List<Object[]> getSelectionSummaryByType(@Param("caseObj") VerificationCase verificationCase);
    
    // Delete all selections for a case (useful for reprocessing)
    @Modifying
    @Transactional
    @Query("DELETE FROM VerificationCaseSelection vcs WHERE vcs.verificationCase = :caseObj")
    void deleteByVerificationCase(@Param("caseObj") VerificationCase verificationCase);
    
    
}