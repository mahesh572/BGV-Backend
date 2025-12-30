package com.org.bgv.vendor.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationDocumentDTO {
	private String id;
    private String name;
    private String originalName;
    private String type;
    private String documentCategory;
    private String size;
    private String uploadedBy;
    private LocalDateTime uploadedAt;
    private String status;
    private String url;
    private String documentPath;
    private String mimeType;
    private String verificationNotes;
    private String comments;
    private boolean verified;
    private LocalDateTime verifiedAt;
    private String verifiedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields for verification case document info
    private Boolean isAddOn;
    private Boolean required;
    private Double documentPrice;
    private String verificationStatus;
}
