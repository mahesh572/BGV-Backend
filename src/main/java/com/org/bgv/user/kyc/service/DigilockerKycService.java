package com.org.bgv.user.kyc.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.org.bgv.config.SecurityUtils;
import com.org.bgv.constants.DocumentType;
import com.org.bgv.entity.User;
import com.org.bgv.exceptions.BusinessException;
import com.org.bgv.service.util.UserServiceUtil;
import com.org.bgv.user.enums.KycProvider;
import com.org.bgv.user.enums.KycVerificationStatus;
import com.org.bgv.user.kyc.emums.DigiLockerAccountStatus;
import com.org.bgv.user.kyc.emums.DigiLockerSessionStatus;
import com.org.bgv.user.kyc.emums.DigiLockerUserFlow;
import com.org.bgv.user.kyc.entity.DigilockerVerificationSession;
import com.org.bgv.user.kyc.entity.UserKycVerification;
import com.org.bgv.user.kyc.repository.DigilockerVerificationSessionRepository;
import com.org.bgv.user.kyc.repository.UserKycVerificationRepository;
import com.org.bgv.user.kyc.requests.CreateDigilockerSessionRequest;
import com.org.bgv.user.kyc.requests.VerifyDigilockerAccountRequest;
import com.org.bgv.user.kyc.response.CreateDigilockerSessionResponse;
import com.org.bgv.user.kyc.response.DigilockerStatusResponse;
import com.org.bgv.user.kyc.response.VerifyDigilockerAccountResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DigilockerKycService {

    private final DigilockerProviderFactory providerFactory;

    private final DigilockerVerificationSessionRepository sessionRepository;

    private final UserKycVerificationRepository kycRepository;
    
    private final UserServiceUtil userServiceUtil;

    
    
    @Transactional
    public CreateDigilockerSessionResponse startVerification(
            VerifyDigilockerAccountRequest request) {

        Long userId = SecurityUtils.getCurrentUserId();

        log.info("Starting DigiLocker verification for userId={}", userId);

        validateRequest(request);
        log.debug("Request validation completed for userId={}", userId);

        DigilockerVerificationProvider provider = providerFactory.getProvider();
        log.debug("Using DigiLocker provider={}", provider.getClass().getSimpleName());

        /*
         * Step 1
         * Verify Digilocker Account
         */
        log.info("Verifying DigiLocker account for userId={}", userId);
        
        String uuid = UUID.randomUUID().toString();
        
        request.setVerificationId(uuid);

        VerifyDigilockerAccountResponse verifyResponse =
                provider.verifyAccount(request);

        log.info(
                "DigiLocker account verification completed. userId={}, verificationId={}, status={}",
                userId,
                verifyResponse.getVerificationId(),
                verifyResponse.getStatus());

        /*
         * Step 2
         * Decide User Flow
         */
        String userFlow =
                "ACCOUNT_EXISTS".equals(verifyResponse.getStatus())
                        ? "signin"
                        : "signup";

        log.info(
                "Determined DigiLocker user flow. userId={}, flow={}",
                userId,
                userFlow);

        /*
         * Step 3
         * Create Verification Session
         */
        CreateDigilockerSessionRequest sessionRequest =
                CreateDigilockerSessionRequest.builder()
                        .verificationId(verifyResponse.getVerificationId())
                        .documentRequested(List.of("AADHAAR"))
                        .redirectUrl("https://localhost:5173/user/kyc/aadhaar/callback?verification_id="+verifyResponse.getVerificationId())
                        .userFlow(userFlow)
                        .build();

        log.info(
                "Creating DigiLocker verification session. userId={}, verificationId={}",
                userId,
                verifyResponse.getVerificationId());

        CreateDigilockerSessionResponse sessionResponse =
                provider.createSession(sessionRequest);

        log.info(
                "DigiLocker session created successfully. userId={}, referenceId={}, verificationId={}",
                userId,
                sessionResponse.getReferenceId(),
                verifyResponse.getVerificationId());

        /*
         * Step 4
         * Save Session
         */
        log.info(
                "Saving DigiLocker verification session. userId={}, referenceId={}",
                userId,
                sessionResponse.getReferenceId());

        saveSession(
                userId,
                verifyResponse,
                sessionResponse);

        log.debug(
                "DigiLocker session saved successfully. userId={}, referenceId={}",
                userId,
                sessionResponse.getReferenceId());

        /*
         * Step 5
         * Create KYC Record
         */
        log.info(
                "Creating/updating KYC record. userId={}, referenceId={}",
                userId,
                sessionResponse.getReferenceId());

        createOrUpdateKyc(
                userId,
                sessionResponse.getReferenceId());

        log.info(
                "DigiLocker verification initiated successfully. userId={}, referenceId={}",
                userId,
                sessionResponse.getReferenceId());

        return sessionResponse;
    }
    
    private void saveSession(
            Long userId,
            VerifyDigilockerAccountResponse verifyResponse,
            CreateDigilockerSessionResponse sessionResponse) {

    	DigilockerVerificationSession session = DigilockerVerificationSession.builder()
                .userId(userId)
                .documentType(DocumentType.AADHAAR)
                .verificationId(sessionResponse.getVerificationId())
                .referenceId(sessionResponse.getReferenceId())
                .digilockerId(verifyResponse.getDigilockerId())
                .consentUrl(sessionResponse.getUrl())
                .redirectUrl(sessionResponse.getRedirectUrl())
                .userFlow(DigiLockerUserFlow.valueOf(
                        sessionResponse.getUserFlow().toUpperCase()))
                .accountStatus(DigiLockerAccountStatus.valueOf(
                        verifyResponse.getStatus().toUpperCase()))
                .sessionStatus(DigiLockerSessionStatus.PENDING)
               // .otpVerified(false)
               // .retryCount(0)
              //  .expiresAt(LocalDateTime.now().plusMinutes(30)) // Session expires in 30 mins
                .build();

        DigilockerVerificationSession savedSession = sessionRepository.save(session);
    }
    
    
    private void createOrUpdateKyc(
            Long userId,
            Long referenceId) {
    	
    	User user = userServiceUtil.getUserById(userId);

        UserKycVerification verification =
                kycRepository
                .findByUserUserIdAndDocumentType(
                        userId,
                        DocumentType.AADHAAR)
                .orElse(new UserKycVerification());
        
        

        verification.setUser(user);

        verification.setDocumentType(
                DocumentType.AADHAAR);

        verification.setProvider(
                KycProvider.CASHFREE);

        verification.setProviderReference(
                String.valueOf(referenceId));

        verification.setStatus(
                KycVerificationStatus.PENDING);

        kycRepository.save(verification);
    }
    
    @Transactional
    public DigilockerStatusResponse checkStatus(String verificationId) {

        log.info("Checking Digilocker status. verificationId={}", verificationId);

        DigilockerVerificationSession session =
                sessionRepository.findByVerificationId(verificationId)
                        .orElseThrow(() -> {
                            log.error("Digilocker session not found. verificationId={}", verificationId);
                            return new BusinessException("Digilocker session not found.");
                        });

        log.info("Found Digilocker session. verificationId={}, referenceId={}, currentStatus={}",
                session.getVerificationId(),
                session.getReferenceId(),
                session.getSessionStatus());

        DigilockerVerificationProvider provider =
                providerFactory.getProvider();

        log.info("Calling Digilocker provider. verificationId={}, referenceId={}",
                session.getVerificationId(),
                session.getReferenceId());

        DigilockerStatusResponse response =
                provider.getStatus(
                        session.getVerificationId(),
                        session.getReferenceId());

        log.info("Received Digilocker status. verificationId={}, status={}",
                response.getVerificationId(),
                response.getStatus());

        updateSessionStatus(session, response);

        if (DigiLockerSessionStatus.AUTHENTICATED.name()
                .equalsIgnoreCase(response.getStatus())) {

            log.info("Digilocker authentication completed. verificationId={}",
                    session.getVerificationId());

            markVerificationCompleted(session);
        }

        log.info("Completed Digilocker status check. verificationId={}, finalStatus={}",
                session.getVerificationId(),
                session.getSessionStatus());

        return response;
    }

    private void updateSessionStatus(
            DigilockerVerificationSession session,
            DigilockerStatusResponse response) {

        log.info("Updating Digilocker session. verificationId={}, oldStatus={}, newStatus={}",
                session.getVerificationId(),
                session.getSessionStatus(),
                response.getStatus());

        session.setSessionStatus(
                DigiLockerSessionStatus.valueOf(
                        response.getStatus().toUpperCase()));

        // session.setDigilockerId(response.getDigilockerId());

        if (session.getSessionStatus() == DigiLockerSessionStatus.AUTHENTICATED) {

            session.setAuthenticatedAt(LocalDateTime.now());

            log.info("Authentication timestamp updated. verificationId={}, authenticatedAt={}",
                    session.getVerificationId(),
                    session.getAuthenticatedAt());
        }

        sessionRepository.save(session);

        log.info("Digilocker session saved successfully. verificationId={}, status={}",
                session.getVerificationId(),
                session.getSessionStatus());
    }
    
    private void markVerificationCompleted(
            DigilockerVerificationSession session) {

        log.info("Updating KYC verification. userId={}, documentType={}, verificationId={}",
                session.getUserId(),
                session.getDocumentType(),
                session.getVerificationId());

        UserKycVerification verification =
                kycRepository
                        .findByUserUserIdAndDocumentType(
                                session.getUserId(),
                                session.getDocumentType())
                        .orElseThrow(() -> {
                            log.error("User KYC record not found. userId={}, documentType={}",
                                    session.getUserId(),
                                    session.getDocumentType());

                            return new BusinessException(
                                    "User KYC record not found.");
                        });

        log.info("KYC record found. userId={}, currentStatus={}",
                session.getUserId(),
                verification.getStatus());

        verification.setStatus(KycVerificationStatus.VERIFIED);
        verification.setVerifiedAt(LocalDateTime.now());
        verification.setProviderReference(
                String.valueOf(session.getReferenceId()));
        verification.setRemarks(
                "Verified successfully using DigiLocker.");

        log.info("Saving KYC verification. userId={}, newStatus={}, providerReference={}",
                session.getUserId(),
                verification.getStatus(),
                verification.getProviderReference());

        kycRepository.save(verification);

        log.info("KYC verification updated successfully. userId={}, documentType={}, verifiedAt={}",
                session.getUserId(),
                session.getDocumentType(),
                verification.getVerifiedAt());
    }
    
    private void validateRequest(
            VerifyDigilockerAccountRequest request) {

        if (request == null) {
            throw new BusinessException("Request cannot be null.");
        }

        if (request.getMobileNumber() == null ||
                request.getMobileNumber().isBlank()) {

            throw new BusinessException("Mobile number is required.");
        }

        if (request.getAadhaarNumber() == null ||
                request.getAadhaarNumber().isBlank()) {

            throw new BusinessException("Aadhaar number is required.");
        }
    }
    
}
