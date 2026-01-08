package com.org.bgv.common;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
@Data
public class ConsentRequest {
    private Long candidateId;
    private String consentType; // "SIGNATURE", "FILE_UPLOAD", or "BOTH"
    private String policyVersion;
    
    // For signature (canvas data as JSON string)
    private String signatureData;
    
    // For file upload
    private MultipartFile consentFile;
    
    // These will be set automatically in controller
    private String ipAddress;
    private String userAgent;
}