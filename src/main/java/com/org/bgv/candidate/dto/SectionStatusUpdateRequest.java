package com.org.bgv.candidate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class SectionStatusUpdateRequest {
    
    @NotNull(message = "Candidate ID is required")
    private Long candidateId;
    
    @NotBlank(message = "Section is required")
    private String section;
    
    @NotBlank(message = "Status is required")
    private String status;
    
    // Optional data field for additional section data
    private Object data;
    
    
}