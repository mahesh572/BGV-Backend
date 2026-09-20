package com.org.bgv.user.kyc.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyDigilockerAccountResponse {

    @JsonProperty("verification_id")
    private String verificationId;

    @JsonProperty("reference_id")
    private Long referenceId;

    @JsonProperty("mobile_number")
    private String mobileNumber;

    @JsonProperty("aadhaar_number")
    private String aadhaarNumber;

    @JsonProperty("status")
    private String status;

    @JsonProperty("digilocker_id")
    private String digilockerId;
}
