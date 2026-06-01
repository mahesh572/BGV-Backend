package com.org.bgv.vendor.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.vendor.entity.VerificationMethodExecutionEvidence;

@Repository
public interface VerificationMethodExecutionEvidenceRepository
        extends JpaRepository<VerificationMethodExecutionEvidence, Long> {

    List<VerificationMethodExecutionEvidence> findByMethodExecutionId(Long methodExecutionId);

    List<VerificationMethodExecutionEvidence> findByDocumentId(Long documentId);

    List<VerificationMethodExecutionEvidence> findByUploadedBy(Long uploadedBy);

    List<VerificationMethodExecutionEvidence> findByArchivedFalse();
}