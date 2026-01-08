package com.org.bgv.vendor.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationContextDTO {
    private String verificationStage; // document_check, university_contact, portal_verification
    private String universityContactDetails;
    private String portalUrl;
    private String verificationOfficer;
    private List<String> verificationChannels; // email, phone, portal, in_person
    private String difficultyLevel; // easy, medium, hard
    private Boolean isInternational;
    private Boolean requiresTranslation;
}
