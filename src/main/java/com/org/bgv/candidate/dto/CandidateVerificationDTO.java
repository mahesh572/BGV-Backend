package com.org.bgv.candidate.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.org.bgv.constants.VerificationStatus;

import lombok.Builder;
import lombok.Data;

@Data
//@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CandidateVerificationDTO {
    private Long id;
    private Long candidateId;
    private Long packageId;
    private String packageName;
    private String employerName;
    private String employerId;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dueDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;
    
    private VerificationStatus status;
    private Integer progressPercentage;
    
    private Map<String, VerificationSectionDTO> sections;
    private String instructions;
    private String supportEmail;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime submittedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime completedAt;
    
    private String verificationNotes;
}
