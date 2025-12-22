package com.org.bgv.candidate.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.org.bgv.constants.SectionStatus;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VerificationSectionDTO {
    private String sectionId; // basicDetails, identity, education, etc.
    private String label;
    private boolean required;
    private SectionStatus status;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastUpdated;
    
    private Integer completionPercentage;
    private String validationErrors;
    private String reviewerNotes;
    
    // Section-specific data
    private Object data;
}