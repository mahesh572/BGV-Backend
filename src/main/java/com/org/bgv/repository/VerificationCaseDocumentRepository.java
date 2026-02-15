package com.org.bgv.repository;

import com.org.bgv.common.DocumentStatus;
import com.org.bgv.constants.VerificationStatus;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseDocument;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VerificationCaseDocumentRepository extends JpaRepository<VerificationCaseDocument, Long> {
    
    List<VerificationCaseDocument> findByVerificationCaseCaseId(Long caseId);
    
    List<VerificationCaseDocument> findByVerificationCaseCaseIdAndVerificationStatus(Long caseId, VerificationStatus status);
    
    @Query("SELECT ccd FROM VerificationCaseDocument ccd WHERE ccd.verificationCase.caseId = :caseId AND ccd.isAddOn = :isAddOn")
    List<VerificationCaseDocument> findByCaseIdAndAddOnStatus(@Param("caseId") Long caseId, 
                                                         @Param("isAddOn") Boolean isAddOn);
    
    Optional<VerificationCaseDocument> findByVerificationCaseCaseIdAndDocumentTypeDocTypeId(Long caseId, Long documentTypeId);
    
     List<VerificationCaseDocument> findByVerificationCaseCaseIdAndCheckCategoryCategoryId(Long caseId,Long categoryId);
    
    @Query("SELECT COUNT(ccd) FROM VerificationCaseDocument ccd WHERE ccd.verificationCase.caseId = :caseId AND ccd.verificationStatus = :status")
    Long countByCaseIdAndVerificationStatus(@Param("caseId") Long caseId, 
                                          @Param("status") VerificationStatus status);
    
    @Query("SELECT ccd FROM VerificationCaseDocument ccd WHERE ccd.verificationCase.candidateId = :candidateId AND ccd.verificationStatus = 'PENDING'")
    List<VerificationCaseDocument> findPendingDocumentsByCandidate(@Param("candidateId") Long candidateId);
    
    Integer countByVerificationCase(VerificationCase verificationCase);
    
    List<VerificationCaseDocument> findByVerificationCaseAndCheckCategory(VerificationCase verificationCase, CheckCategory checkType);
    
    List<VerificationCaseDocument> findByVerificationCaseAndCheckCategory_Code(
            VerificationCase verificationCase, String code);
    
    
    Optional<VerificationCaseDocument> findByVerificationCase_CaseIdAndCheckCategory_CategoryIdAndDocumentType_DocTypeId(
            Long caseId, Long categoryId, Long documentTypeId);
    
    Optional<VerificationCaseDocument> findByVerificationCase_CaseIdAndVerificationCaseCheck_CaseCheckIdAndDocumentType_DocTypeId(Long caseId,Long checkId,Long documentTypeId);
    
    List<VerificationCaseDocument> findByVerificationCase_CaseIdAndVerificationCaseCheck_CaseCheckId(Long caseId,Long checkId);
    
    
    long countByVerificationCaseCheckCaseCheckIdAndRequiredTrue(Long caseCheckId);

    long countByVerificationCaseCheckCaseCheckIdAndRequiredTrueAndVerificationStatus(
            Long caseCheckId,
            DocumentStatus status
    );

    
}