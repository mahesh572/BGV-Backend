package com.org.bgv.vendor.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EducationVerificationData {

    private String institutionName;
    private String degree;
    private String specialization;
    private String registerNumber;
    private String graduationYear;
}
