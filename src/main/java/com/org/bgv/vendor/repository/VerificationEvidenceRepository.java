package com.org.bgv.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.org.bgv.common.EvidenceLevel;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.vendor.entity.VerificationEvidence;

@Repository
public interface VerificationEvidenceRepository extends JpaRepository<VerificationEvidence, Long> {
    List<VerificationEvidence> findByVerificationCaseCheck(VerificationCaseCheck check);
    
    List<VerificationEvidence> findByVerificationCaseCheck_CaseCheckId(Long caseCheckId);

    List<VerificationEvidence> findByVerificationCaseCheck_CaseCheckIdAndEvidenceLevel(
            Long caseCheckId,
            EvidenceLevel level
    );

    List<VerificationEvidence> findByVerificationCaseCheck_CaseCheckIdAndDocumentType_DocTypeId(
            Long caseCheckId,
            Long documentTypeId
    );
    
    
    @Query("""
    		SELECT e FROM VerificationEvidence e
    		JOIN FETCH e.verificationCaseCheck c
    		LEFT JOIN FETCH e.documentType d
    		WHERE c.id = :checkId
    		AND e.status != 'ARCHIVED'
    		""")
    		List<VerificationEvidence> findAllByCheckId(Long checkId);

}
