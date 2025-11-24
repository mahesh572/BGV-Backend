package com.org.bgv.repository;

import com.org.bgv.entity.IdentityProof;
import com.org.bgv.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdentityProofRepository extends JpaRepository<IdentityProof, Long> {

    // Find all proofs for a specific profile
    List<IdentityProof> findByProfile(Profile profile);

    // Find all proofs by profile ID (alternative if you don't have Profile object)
    List<IdentityProof> findByProfile_ProfileId(Long profileId);

    // Find proofs by profile and status (e.g. PENDING, VERIFIED, REJECTED)
    List<IdentityProof> findByProfile_ProfileIdAndStatus(Long profileId, String status);

    // Check if a document number already exists for a profile (avoid duplicates)
    boolean existsByProfile_ProfileIdAndDocumentNumber(Long profileId, String documentNumber);
    void deleteByProfile_ProfileId(Long profileId); //
    
 // Find all proofs by candidate ID
    List<IdentityProof> findByCandidate_CandidateId(Long candidateId);
    


 // Correct method name - using camelCase for docTypeId
    List<IdentityProof> findByCandidateCandidateIdAndDocTypeId(Long candidateId, Long docTypeId);

}
