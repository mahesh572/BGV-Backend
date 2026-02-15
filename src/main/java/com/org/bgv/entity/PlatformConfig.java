package com.org.bgv.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "platform_config")
@Data
public class PlatformConfig {

    @Id
    private Long id; // always 1 (singleton)

    // Branding
    private String platformBrandName;   // VerifyPro
    private String platformLegalName;    // VerifyPro Technologies Pvt Ltd
    private String platformLogoUrl;
    private String platformLogoKey;

    // Email
    private String defaultFromName;      // VerifyPro Team
    private String defaultFromEmail;     // no-reply@verifypro.com
    private String supportEmail;

    // Website
    private String websiteUrl;

    // Legal
    private String privacyPolicyUrl;
    private String termsUrl;

    // Audit
    private LocalDateTime updatedAt;
}

