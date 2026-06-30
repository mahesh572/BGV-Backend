package com.org.bgv.user.kyc.requests;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PanVerificationRequest {
    private String panNumber;
    private String fullName;
    private String dob;
}