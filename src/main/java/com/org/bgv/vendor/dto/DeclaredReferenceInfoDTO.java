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
public class DeclaredReferenceInfoDTO {
    private String referenceType; // professional, personal, academic
    private String name;
    private String designation;
    private String organization;
    private String email;
    private String phone;
    private String relationship;
    private Integer yearsKnown;
    private LocalDateTime declaredOn;
    private String verificationMethod;
    private Boolean canContact;
    private String preferredContactTime;
}