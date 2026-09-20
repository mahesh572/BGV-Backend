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
public class CreateDigilockerSessionResponse {

    @JsonProperty("verification_id")
    private String verificationId;

    @JsonProperty("reference_id")
    private Long referenceId;

    @JsonProperty("url")
    private String url;

    @JsonProperty("status")
    private String status;

    @JsonProperty("document_requested")
    private List<String> documentRequested;

    @JsonProperty("user_flow")
    private String userFlow;

    @JsonProperty("redirect_url")
    private String redirectUrl;
}