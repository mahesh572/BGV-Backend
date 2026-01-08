package com.org.bgv.common;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CandidateDTO {

    @JsonProperty("candidateId")
    @JsonAlias({"id"})
    private Long candidateId;

    private String firstName;
    private String lastName;

    @JsonProperty("email")
    private String email;

    // Accept multiple formats of mobile/phone
    @JsonProperty("mobileNo")
    @JsonAlias({"phoneNumber", "phone_number", "mobile", "mobile_no"})
    private String mobileNo;

    private String gender;
    private String role;
    private String status;
    private String name;

    private String sourceType;
    private Long companyId;
    private Long userId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastActiveAt;

    private Boolean isActive;
    private Boolean isVerified;
    private String verificationStatus;

    private String jobSearchStatus;
    private Boolean isConsentProvided;

    private String companyName;
}
