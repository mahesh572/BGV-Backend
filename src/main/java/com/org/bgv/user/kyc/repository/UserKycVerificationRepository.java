package com.org.bgv.user.kyc.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.constants.DocumentType;
import com.org.bgv.user.kyc.entity.UserKycVerification;


public interface UserKycVerificationRepository
        extends JpaRepository<UserKycVerification, Long> {

    Optional<UserKycVerification> findByUserUserIdAndDocumentType(
            Long userId,
           DocumentType documentType);

    boolean existsByUserUserIdAndDocumentType(
            Long userId,
            DocumentType documentType);
}