package com.org.bgv.vendor.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeclaredIdentityInfoDTO {
    private String documentType; // aadhaar, passport, driving_license
    private String documentNumber;
    private String name;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private String issuingAuthority;
    private String countryOfIssue;
    private LocalDateTime declaredOn;
    private List<String> documentSubtypes;
}