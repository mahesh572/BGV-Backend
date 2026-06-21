package com.org.bgv.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseCheck;

@Repository
public interface VerificationCaseCheckRepository
        extends JpaRepository<VerificationCaseCheck, Long> {
	
	List<VerificationCaseCheck> findByVerificationCase(VerificationCase verificationCase);

    List<VerificationCaseCheck>
    findByVerificationCase_CaseId(Long caseId);

    List<VerificationCaseCheck>
    findByVerificationCase_CaseIdAndStatus(
            Long caseId,
            CaseCheckStatus status);

    Optional<VerificationCaseCheck>
    findByVerificationCase_CaseIdAndCategory_CategoryId(
            Long caseId,
            Long categoryId);
    
    Optional<VerificationCaseCheck>
    findByVerificationCaseAndCategory(
            VerificationCase verificationCase,
            CheckCategory category
    );
    
    VerificationCaseCheck findByVerificationCase_CaseIdAndCaseCheckId(
            Long caseId,
            Long caseCheckId);

    List<VerificationCaseCheck>
    findByCategory_CategoryId(Long categoryId);

    long countByStatus(CaseCheckStatus status);

    List<VerificationCaseCheck>
    findTop10ByVendorCompany_IdOrderByUpdatedAtDesc(Long companyId);

    List<VerificationCaseCheck>
    findByVendorCompany_IdOrderByUpdatedAtDesc(Long companyId);

    long countByVendorCompany_IdAndStatusIn(
            Long companyId,
            List<CaseCheckStatus> statuses);

    List<VerificationCaseCheck>
    findByVendorCompany_IdAndStatusIn(
            Long companyId,
            List<CaseCheckStatus> statuses);

    Optional<VerificationCaseCheck>
    findByVendorCompany_IdAndCaseCheckId(
            Long companyId,
            Long caseCheckId);

    List<VerificationCaseCheck> findByAssignedVendorUser_UserId(Long userId);

    List<VerificationCaseCheck>
    findByAssignedVendorUser_UserIdAndStatusIn(
            Long userId,
            List<CaseCheckStatus> statuses);

    Optional<VerificationCaseCheck>
    findByAssignedVendorUser_UserIdAndCaseCheckId(
            Long userId,
            Long caseCheckId);
}