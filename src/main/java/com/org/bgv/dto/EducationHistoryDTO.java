package com.org.bgv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationHistoryDTO {

    private Long id;
    
    @NotNull(message = "Candidate ID is required")
    private Long candidateId;

    // Instead of mapping full DegreeType entity, use degreeId and optionally degreeName
    private Long qualificationType;
    private String degreeName;  // optional

    // Instead of mapping full FieldOfStudy entity, use fieldId and optionally fieldName
    private Long fieldOfStudy;
    private String fieldName;  // optional
    
    private String grade;
    private Double gpa;
    
    private Long profileId;
    
    private String fromMonth; // e.g., "August"
    private Integer fromYear;
    private String toMonth;
    private Integer toYear;

    private String institutionName;
    private String universityName;
    private String city;
    private String state;
    private String country;
   // private String courseOrField;
    private Integer yearOfPassing;
    private String typeOfEducation;
    
    private boolean verified;
    private String verificationStatus;
    private String verifiedBy;
    
    private List<DocumentResponse> documents;
}
