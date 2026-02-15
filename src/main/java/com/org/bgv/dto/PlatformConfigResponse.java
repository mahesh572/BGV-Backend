package com.org.bgv.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PlatformConfigResponse {

    private Long id;

    // Branding
    private String platformBrandName;
    private String platformLegalName;
    private String websiteUrl;

    // Logo
    private String platformLogoUrl;
    private LocalDateTime logoUploadedAt;

    // Email
    private String supportEmail;
    private String defaultFromName;
    private String defaultFromEmail;

    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
