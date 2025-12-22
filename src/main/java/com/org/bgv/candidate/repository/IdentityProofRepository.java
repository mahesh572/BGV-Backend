package com.org.bgv.candidate.repository;

import com.org.bgv.candidate.entity.IdentityProof;
import com.org.bgv.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IdentityProofRepository extends JpaRepository<IdentityProof, Long> {

    /* ---------------- PROFILE ---------------- */

    List<IdentityProof> findByProfile(Profile profile);

    List<IdentityProof> findByProfile_ProfileId(Long profileId);

    List<IdentityProof> findByProfile_ProfileIdAndStatus(Long profileId, String status);

    boolean existsByProfile_ProfileIdAndDocumentNumber(Long profileId, String documentNumber);

    void deleteByProfile_ProfileId(Long profileId);


    /* ---------------- CANDIDATE ---------------- */

    List<IdentityProof> findByCandidate_CandidateId(Long candidateId);

    Optional<IdentityProof> findByCandidate_CandidateIdAndDocTypeId(
            Long candidateId,
            Long docTypeId
    );

    boolean existsByCandidate_CandidateIdAndDocumentNumber(
            Long candidateId,
            String documentNumber
    );


    /* ---------------- VERIFICATION ---------------- */

    List<IdentityProof> findByVerificationStatus(String verificationStatus);

    List<IdentityProof> findByVerified(boolean verified);

    Long countByCandidate_CandidateIdAndVerifiedTrue(Long candidateId);


    /* ---------------- EXPIRY ---------------- */

    @Query("""
        SELECT i FROM IdentityProof i
        WHERE i.expiryDate < CURRENT_DATE
    """)
    List<IdentityProof> findExpiredDocuments();

    @Query("""
    	    SELECT i FROM IdentityProof i
    	    WHERE i.expiryDate BETWEEN :startDate AND :endDate
    	""")
    	List<IdentityProof> findDocumentsExpiringBetween(
    	        @Param("startDate") LocalDate startDate,
    	        @Param("endDate") LocalDate endDate
    	);
    
    @Query("""
    	    SELECT i FROM IdentityProof i
    	    WHERE i.candidate.candidateId = :candidateId
    	      AND i.docTypeId = :docTypeId
    	""")
    	Optional<IdentityProof> findByCandidateIdAndDocumentType(
    	        @Param("candidateId") Long candidateId,
    	        @Param("docTypeId") Long docTypeId
    	);
}

