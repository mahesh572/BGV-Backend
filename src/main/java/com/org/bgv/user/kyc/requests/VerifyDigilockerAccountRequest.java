package com.org.bgv.user.kyc.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyDigilockerAccountRequest {

	    @JsonProperty("verification_id")
	    private String verificationId;

	    @JsonProperty("mobile_number")
	    private String mobileNumber;

	    @JsonProperty("aadhaar_number")
	    private String aadhaarNumber;
    
}
