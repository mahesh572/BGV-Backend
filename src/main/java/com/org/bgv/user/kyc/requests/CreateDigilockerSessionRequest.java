package com.org.bgv.user.kyc.requests;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDigilockerSessionRequest {

	@JsonProperty("verification_id")
    private String verificationId;

    @JsonProperty("document_requested")
    private List<String> documentRequested;

    @JsonProperty("redirect_url")
    private String redirectUrl;

    @JsonProperty("user_flow")
    private String userFlow;

    @JsonProperty("url")
    private String url;
}
