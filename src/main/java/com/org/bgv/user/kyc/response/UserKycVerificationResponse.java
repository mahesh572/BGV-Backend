package com.org.bgv.user.kyc.response;

import java.time.LocalDateTime;

import com.org.bgv.constants.DocumentType;
import com.org.bgv.user.enums.KycProvider;
import com.org.bgv.user.enums.KycVerificationStatus;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserKycVerificationResponse {

    private DocumentType documentType;

    private KycVerificationStatus status;

    private KycProvider provider;

    private LocalDateTime verifiedAt;

    private String remarks;
}
