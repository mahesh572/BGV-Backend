package com.org.bgv.user.kyc.service;

import org.springframework.stereotype.Component;

import com.org.bgv.user.kyc.entity.UserKycVerification;
import com.org.bgv.user.kyc.response.UserKycVerificationResponse;

@Component
public class UserKycVerificationMapper {

    public UserKycVerificationResponse toResponse(
            UserKycVerification verification) {

        return UserKycVerificationResponse.builder()
                .documentType(verification.getDocumentType())
                .status(verification.getStatus())
                .provider(verification.getProvider())
                .verifiedAt(verification.getVerifiedAt())
                .remarks(verification.getRemarks())
                .build();
    }
}
