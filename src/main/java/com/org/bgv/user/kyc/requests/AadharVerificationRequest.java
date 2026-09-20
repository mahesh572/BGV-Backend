package com.org.bgv.user.kyc.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AadharVerificationRequest {
	
	
	@JsonProperty("aadhaar")
    private String aadhaar;

	@JsonProperty("name")
    private String name;
	
	@JsonProperty("mobile")
	private Long mobile;

}
