package com.org.bgv.user.kyc.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CashfreePanResponse {

    @JsonProperty("pan")
    private String pan;

    @JsonProperty("type")
    private String type;

    @JsonProperty("reference_id")
    private Long referenceId;

    @JsonProperty("name_provided")
    private String nameProvided;

    @JsonProperty("registered_name")
    private String registeredName;

    @JsonProperty("father_name")
    private String fatherName;

    @JsonProperty("valid")
    private boolean valid;

    @JsonProperty("message")
    private String message;
}