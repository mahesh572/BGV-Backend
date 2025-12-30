package com.org.bgv.vendor.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdentityContextDTO {
    private String verificationStage; // document_authentication, biometric_verification
    private String verificationAuthority;
    private Boolean isDigitalVerificationPossible;
    private List<String> acceptedDocuments;
    private Boolean requiresPhysicalVerification;
    private String biometricMethod; // fingerprint, face_recognition
    private String governmentPortalUrl;
}