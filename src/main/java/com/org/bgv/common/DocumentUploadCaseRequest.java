package com.org.bgv.common;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUploadCaseRequest {
    
    @NotNull
    private Long caseDocumentId;
    
    private String documentUrl;
    
    private String notes;
}
