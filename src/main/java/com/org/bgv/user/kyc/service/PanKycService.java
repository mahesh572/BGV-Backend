package com.org.bgv.user.kyc.service;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.org.bgv.config.SecurityUtils;
import com.org.bgv.constants.DocumentType;
import com.org.bgv.entity.User;
import com.org.bgv.exceptions.BusinessException;
import com.org.bgv.service.UserService;
import com.org.bgv.service.util.UserServiceUtil;
import com.org.bgv.user.enums.KycProvider;
import com.org.bgv.user.enums.KycVerificationStatus;
import com.org.bgv.user.kyc.entity.UserKycVerification;
import com.org.bgv.user.kyc.repository.UserKycVerificationRepository;
import com.org.bgv.user.kyc.requests.PanVerificationRequest;
import com.org.bgv.user.kyc.requests.PanVerificationResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PanKycService {

    private static final Pattern PAN_PATTERN =
            Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]$");

    private final PanProviderFactory providerFactory;
    private final UserKycVerificationRepository userKycVerificationRepository;
    private final UserServiceUtil userServiceUtil;

    public PanVerificationResponse verify(PanVerificationRequest request) {

        validate(request);
        
        Long userId = SecurityUtils.getCurrentUserId();
        User user = userServiceUtil.getUserById(userId);
        
        UserKycVerification verification =
        		userKycVerificationRepository.findByUserUserIdAndDocumentType(
                        userId,
                        DocumentType.PAN_CARD)
                .orElse(new UserKycVerification());
        
        
        PanVerificationResponse response =
                providerFactory.getProvider().verify(request);

        verification.setUser(user);
        verification.setDocumentType(DocumentType.PAN_CARD);
        verification.setProvider(KycProvider.CASHFREE);
        verification.setProviderReference(response.getProviderReference());
        verification.setVerifiedAt(LocalDateTime.now());

        if (response.isVerified()) {
            verification.setStatus(KycVerificationStatus.VERIFIED);
        } else {
            verification.setStatus(KycVerificationStatus.FAILED);
        }

        userKycVerificationRepository.save(verification);

        return response;
    }

    private void validate(PanVerificationRequest request) {

        if (request == null) {
            throw new BusinessException("Request cannot be null.");
        }

        if (!StringUtils.hasText(request.getPan())) {
            throw new BusinessException("PAN number is required.");
        }

        if (!StringUtils.hasText(request.getName())) {
            throw new BusinessException("Full name is required.");
        }

        String pan = request.getPan().trim().toUpperCase();

        if (!PAN_PATTERN.matcher(pan).matches()) {
            throw new BusinessException("Invalid PAN number.");
        }

        // Normalize PAN before sending to provider
        request.setPan(pan);
    }
}