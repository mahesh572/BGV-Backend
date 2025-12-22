package com.org.bgv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IdentityProofDTO {
    private Long id;
    
    @NotNull(message = "Candidate ID is required")
    private Long candidateId;
   
    @NotBlank(message = "Document type is required")
    @Size(max = 50, message = "Document type cannot exceed 50 characters")
    private String documentType; // AADHAR, PAN, PASSPORT, etc.
    
    private Long documentTypeId;
    
    private String documentNumber;
    
    @NotNull(message = "Issue date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate issueDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiryDate;
    
    private boolean verified = false;
    
    private String verificationStatus = "pending";
    
    private String verifiedBy;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime verifiedAt;
    
 // Derived fields
    private boolean documentExpired;
    private Long daysUntilExpiry;
    private Integer ageInYears;
    
    private String status;
    private LocalDateTime uploadedAt;
    private String updatedBy;

    private List<DocumentResponse> documents;
   // private DocumentStats documentStats;
}
