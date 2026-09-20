package com.org.bgv.user.kyc.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DigilockerStatusResponse {

	@JsonProperty("user_details")
	private DigilockerUserDetailsResponse userDetails;

	@JsonProperty("document_requested")
	private List<String> documentRequested;

	@JsonProperty("document_consent")
	private List<String> documentConsent;

	@JsonProperty("document_consent_validity")
	private String documentConsentValidity;

	@JsonProperty("verification_id")
	private String verificationId;

	@JsonProperty("reference_id")
	private Long referenceId;
	
	private String status;
}
