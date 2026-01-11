package com.org.bgv.common;

import com.org.bgv.constants.VerificationStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationUpdateRequest {
    
    @NotNull
    private Long caseDocumentId;
    
    @NotNull
    private DocumentStatus status;
    
    private String verificationNotes;
}