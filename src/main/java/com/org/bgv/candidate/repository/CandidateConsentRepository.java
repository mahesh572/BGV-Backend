package com.org.bgv.candidate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.CandidateConsent;
import com.org.bgv.entity.CandidateConsent.ConsentType;

@Repository
public interface CandidateConsentRepository extends JpaRepository<CandidateConsent, Long> {
    
	List<CandidateConsent> findByCandidateCandidateId(Long candidateId);
	
    List<CandidateConsent> findByCandidateCandidateIdAndConsentType(Long candidateId, CandidateConsent.ConsentType consentType);
    
    @Modifying
    @Query("""
        UPDATE CandidateConsent c
        SET c.status = 'REVOKED', c.revokedAt = CURRENT_TIMESTAMP
        WHERE c.candidate.candidateId = :candidateId
          AND c.consentType = :type
          AND c.status = 'ACTIVE'
    """)
    void revokeActiveConsent(Long candidateId, ConsentType type);

}