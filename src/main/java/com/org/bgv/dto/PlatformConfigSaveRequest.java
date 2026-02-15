package com.org.bgv.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PlatformConfigSaveRequest {

    // 🔹 Branding
    @NotBlank
    private String platformBrandName;     // "Mayuktha"

    @NotBlank
    private String platformLegalName;      // "Mayuktha Technologies Pvt Ltd"

    private String websiteUrl;

    // 🔹 Email / Communication
    @Email
    private String supportEmail;

    @NotBlank
    private String defaultFromName;        // "Mayuktha Team"

    @Email
    @NotBlank
    private String defaultFromEmail;       // "no-reply@mayuktha.com"

}