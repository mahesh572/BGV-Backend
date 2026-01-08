package com.org.bgv.candidate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.org.bgv.constants.CaseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationCaseDTO {
 private Long caseId;
 private Long candidateId;
 private Long companyId;
 private String companyName;
 private String companyLogo;
 private CaseStatus status;
 
 @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
 private LocalDateTime createdAt;
 
 @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
 private LocalDateTime updatedAt;
 
 @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
 private LocalDateTime completedAt;
 
 // Verification package details
 private String verificationType;
 
 // Progress tracking
 private Integer documentsCount;
 private Integer checksCompleted;
 private Integer totalChecks;
 
 // Vendor information
 private Long vendorId;
 private String vendorName;
 
 // Additional candidate-relevant info
 private String candidateName;
 private String candidateEmail;
}


