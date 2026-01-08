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
public class DeclaredEducationInfoDTO {
    private String degree;
    private String institution;
    private String specialization;
    private String yearOfPassing;
    private String percentage;
    private String grade;
    private String rollNumber;
    private String duration;
    private String location;
    private String country;
    private String universityType;
    private String accreditation;
    private LocalDateTime declaredOn;
    private String verificationMethod;
}
