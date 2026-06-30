package com.org.bgv.user.kyc.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanVerificationResponse {
    private boolean valid;
    private String name;
    private String dob;
    private String status; // ACTIVE / INVALID
    private String providerRefId;
}
