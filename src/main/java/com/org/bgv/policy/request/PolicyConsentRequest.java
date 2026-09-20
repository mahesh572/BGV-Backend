package com.org.bgv.policy.request;

import org.springframework.web.multipart.MultipartFile;

import com.org.bgv.policy.enums.EntityType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PolicyConsentRequest {

    @NotBlank
    private String policyVersionId;

    @NotNull
    private Long entityId;

    @NotNull
    private EntityType entityType;

    /**
     * Canvas Base64
     */
    private String signatureData;

    /**
     * Camera Photo
     */
    private MultipartFile livePhoto;

    /**
     * Filled by Controller
     */
    private String ipAddress;

    private String userAgent;
}