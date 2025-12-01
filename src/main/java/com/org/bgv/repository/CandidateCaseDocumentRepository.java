package com.org.bgv.repository;

import com.org.bgv.constants.VerificationStatus;
import com.org.bgv.entity.CandidateCaseDocument;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateCaseDocumentRepository extends JpaRepository<CandidateCaseDocument, Long> {
    
    List<CandidateCaseDocument> findByCandidateCaseCaseId(Long caseId);
    
    List<CandidateCaseDocument> findByCandidateCaseCaseIdAndVerificationStatus(Long caseId, VerificationStatus status);
    
    @Query("SELECT ccd FROM CandidateCaseDocument ccd WHERE ccd.candidateCase.caseId = :caseId AND ccd.isAddOn = :isAddOn")
    List<CandidateCaseDocument> findByCaseIdAndAddOnStatus(@Param("caseId") Long caseId, 
                                                         @Param("isAddOn") Boolean isAddOn);
    
    Optional<CandidateCaseDocument> findByCandidateCaseCaseIdAndDocumentTypeDocTypeId(Long caseId, Long documentTypeId);
    
    @Query("SELECT COUNT(ccd) FROM CandidateCaseDocument ccd WHERE ccd.candidateCase.caseId = :caseId AND ccd.verificationStatus = :status")
    Long countByCaseIdAndVerificationStatus(@Param("caseId") Long caseId, 
                                          @Param("status") VerificationStatus status);
    
    @Query("SELECT ccd FROM CandidateCaseDocument ccd WHERE ccd.candidateCase.candidateId = :candidateId AND ccd.verificationStatus = 'PENDING'")
    List<CandidateCaseDocument> findPendingDocumentsByCandidate(@Param("candidateId") Long candidateId);
}