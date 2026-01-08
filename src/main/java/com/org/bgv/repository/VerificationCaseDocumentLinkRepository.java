package com.org.bgv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.entity.VerificationCaseDocument;
import com.org.bgv.entity.VerificationCaseDocumentLink;

@Repository
public interface VerificationCaseDocumentLinkRepository extends JpaRepository<VerificationCaseDocumentLink, Long> {
	
	List<VerificationCaseDocumentLink> findByCaseDocument(VerificationCaseDocument caseDocument);
	
	List<VerificationCaseDocumentLink> findByDocument_DocId(Long documentId);
	
	List<VerificationCaseDocumentLink> findByCaseDocument_CaseDocumentId(Long caseDocumentId);
	
	// Find active links by case ID, category ID, and document type ID
    List<VerificationCaseDocumentLink> findByCaseDocument_VerificationCase_CaseIdAndCaseDocument_CheckCategory_CategoryIdAndCaseDocument_DocumentType_DocTypeIdAndStatusNot(
        Long caseId, Long categoryId, Long docTypeId, String status);

}
