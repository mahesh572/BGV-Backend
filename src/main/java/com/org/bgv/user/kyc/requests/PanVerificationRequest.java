package com.org.bgv.user.kyc.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanVerificationRequest {

	@JsonProperty("pan")
    private String pan;

	@JsonProperty("name")
    private String name;
}