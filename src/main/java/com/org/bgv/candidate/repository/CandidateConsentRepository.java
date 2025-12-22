package com.org.bgv.candidate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.CandidateConsent;

@Repository
public interface CandidateConsentRepository extends JpaRepository<CandidateConsent, Long> {
    List<CandidateConsent> findByCandidateId(Long candidateId);
    List<CandidateConsent> findByCandidateIdAndConsentType(Long candidateId, CandidateConsent.ConsentType consentType);
}