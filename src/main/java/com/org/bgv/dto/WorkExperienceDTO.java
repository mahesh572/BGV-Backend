package com.org.bgv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkExperienceDTO {
	
	private Long id;
	
	@NotNull(message = "Candidate ID is required")
    private Long candidateId;
	
    private String companyName;
    private String position;
    private String employmentType;
    
    @NotNull(message = "Start date is required")
    @PastOrPresent(message = "Start date must be in the past or present")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    
    private Boolean currentlyWorking;
    
    @Size(max = 50, message = "Employee ID cannot exceed 50 characters")
    private Long employeeId;
    
    @Email(message = "Manager email should be valid")
    private String managerEmail;
   
    @Email(message = "HR email should be valid")
    private String hrEmail;
    private String reasonForLeaving;
    
    @Size(max = 50, message = "Notice period cannot exceed 50 characters")
    private String noticePeriod;
    
    private String companyAddress;
    
   
    private String city;
    private String country;
    private String state;
    
    private boolean verified;
    private String verificationStatus;
    private String verifiedBy;
   
   
    private List<DocumentResponse> documents;
  //  private DocumentStats documentStats;
}