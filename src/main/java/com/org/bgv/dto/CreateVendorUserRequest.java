package com.org.bgv.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVendorUserRequest {

    /*
     * Personal Information
     */
    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private LocalDate dateOfBirth;

    private String gender;

    private String nationality;

    private String profilePictureKey;


    /*
     * Operational Information
     */

    // FIELD_INVESTIGATOR
    // EMPLOYMENT_VERIFIER
    // EDUCATION_VERIFIER
    // ADDRESS_VERIFIER
    // QUALITY_ANALYST
    // CASE_ALLOCATOR
    // TEAM_LEAD
    private String operationalRole;

    private Integer experienceYears;

    // StateRegion ids
    private List<Long> regionIds;

    // ENGLISH,HINDI,TELUGU...
    private List<String> languagesSupported;

    private Boolean fieldVerificationAvailable;

    private Boolean vehicleAvailable;

    private Integer maxDailyCapacity;

    // AVAILABLE,BUSY,ON_LEAVE
    private String availabilityStatus;


    /*
     * Access Information
     */
    private List<Long> roleIds;


    /*
     * Account Settings
     */
    private String status;

    private Boolean passwordResetRequired;

    private Boolean sendWelcomeEmail;

}
