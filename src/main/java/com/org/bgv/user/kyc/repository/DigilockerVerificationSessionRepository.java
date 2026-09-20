package com.org.bgv.user.kyc.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.constants.DocumentType;
import com.org.bgv.user.kyc.emums.DigiLockerSessionStatus;
import com.org.bgv.user.kyc.entity.DigilockerVerificationSession;

@Repository
public interface DigilockerVerificationSessionRepository
        extends JpaRepository<DigilockerVerificationSession, Long> {

    Optional<DigilockerVerificationSession> findByVerificationId(
            String verificationId);

    Optional<DigilockerVerificationSession> findByReferenceId(
            Long referenceId);

    Optional<DigilockerVerificationSession> findByVerificationIdAndReferenceId(
            String verificationId,
            Long referenceId);

    List<DigilockerVerificationSession> findByUserIdOrderByCreatedAtDesc(
            Long userId);

    Optional<DigilockerVerificationSession> findTopByUserIdAndDocumentTypeOrderByCreatedAtDesc(
            Long userId,
            DocumentType documentType);
    
    List<DigilockerVerificationSession> findByUserIdAndSessionStatus(
            Long userId,
            DigiLockerSessionStatus sessionStatus);
    /*
    Optional<DigilockerVerificationSession>
    findTopByUserIdAndDocumentTypeAndDeletedFalseOrderByCreatedAtDesc(
            Long userId,
            DocumentType documentType);
            */
}