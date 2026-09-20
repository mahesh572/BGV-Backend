package com.org.bgv.policy.request;

import java.time.LocalDateTime;

import com.org.bgv.policy.enums.PolicyAudience;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicyDetailsResponse {

    private String policyId;
    
    private String policyVersionId;

    private String policyCode;

    private String policyName;

    private String description;

    private PolicyAudience audience;

    private String version;

    private String content;

    private String contentType;

    private LocalDateTime effectiveFrom;

    private String documentUrl;
    
    private Boolean hasAcceptedPolicy;
}
