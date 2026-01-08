package com.org.bgv.common;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class CandidateDetailsDTO {
	
	private Long id;
    private String name;
    
    @JsonProperty("candidateId")
    private Long candidateId;
    
    private String email;
    private String phone;
    
    @JsonProperty("dateOfBirth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy")
    private String dateOfBirth;
    
    private String gender;
    private String pan;
    private String initials;
    
    // Profile specific fields
    private String firstName;
    private String lastName;
    private String nationality;
    private String maritalStatus;
    private Boolean hasWorkExperience;
    private String linkedinUrl;
    
    
    
    private VPackageDTO vpackage;
    
    @JsonProperty("verificationChecks")
    private List<VerificationCheckDTO> verificationChecks;
    
    @JsonProperty("activityTimeline")
    private List<ActivityTimelineDTO> activityTimeline;
}
