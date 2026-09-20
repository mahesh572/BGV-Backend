package com.org.bgv.policy.request;

import java.time.LocalDateTime;

import com.org.bgv.policy.enums.ConsentStatus;
import com.org.bgv.policy.enums.EntityType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
//@NoArgsConstructor
//@AllArgsConstructor
public class PolicyConsentResponse {

    /**
     * Policy Consent Id
     */
  //  private String consentId;

    /**
     * Reference number generated for this consent.
     */
   // private String referenceNumber;

    /**
     * Accepted policy information.
     */
  //  private String policyId;

 //   private String policyCode;

  //  private String policyName;

   // private String policyVersionId;

   // private String policyVersion;

    /**
     * Who accepted the policy.
     */
  //  private Long entityId;

  //  private EntityType entityType;

    /**
     * Consent status.
     */
  //  private Boolean consentGiven;

  //  private ConsentStatus status;

    /**
     * Evidence.
     */
  //  private String signatureUrl;

  //  private String livePhotoUrl;

  //  private String consentPdfUrl;

    /**
     * Audit information.
     */
 //   private LocalDateTime acceptedAt;

  //  private String ipAddress;

  //  private String userAgent;
	
	private String preSignedUrl;
}


