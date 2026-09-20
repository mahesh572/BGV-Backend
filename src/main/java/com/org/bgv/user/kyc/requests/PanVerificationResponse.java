package com.org.bgv.user.kyc.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PanVerificationResponse {

    /**
     * Provider verification status
     */
    private boolean verified;

    /**
     * PAN number returned by provider
     */
    private String pan;

    /**
     * Candidate entered name
     */
    private String providedName;

    /**
     * Name registered with Income Tax
     */
    private String registeredName;

    private String fatherName;

    private String panType;

    private String providerReference;

    private String message;

    /**
     * Keep complete response for auditing/debugging
     */
    private Object rawResponse;
}
