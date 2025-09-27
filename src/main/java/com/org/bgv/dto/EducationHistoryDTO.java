package com.org.bgv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationHistoryDTO {

    private Long edu_id;

    // Instead of mapping full DegreeType entity, use degreeId and optionally degreeName
    private Long degreeId;
    private String degreeName;  // optional

    // Instead of mapping full FieldOfStudy entity, use fieldId and optionally fieldName
    private Long fieldId;
    private String fieldName;  // optional
    
    private String grade;
    private Double gpa;
    
    private Long profileId;
    
    private String fromMonth; // e.g., "August"
    private Integer fromYear;
    private String toMonth;
    private Integer toYear;

    private String instituteName;
    private String universityName;
    private String city;
    private String state;
    private String country;
   // private String courseOrField;
    private Integer yearOfPassing;
    private String typeOfEducation;
    private List<DocumentResponse> documents;
}
