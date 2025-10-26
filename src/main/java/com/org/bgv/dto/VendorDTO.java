package com.org.bgv.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VendorDTO {
	 // Personal Information
    
	// Personal Information
   
    private String firstName;
    
    
    private String lastName;
    
    
    private String email;
    
    
    private String phone;
    
    
    private LocalDate dateOfBirth;
    
    private String gender;
    
    // Professional Information
   
    private String vendorType;
    
    private String experience;
    
    // Services Provided
    private List<Long> servicesProvided; // Array of check type IDs
    
    // Business Information
    private String businessName;
    private String businessType;
    private String registrationNumber;
    private String taxId;
    
    
    private String website;
    
    // Address Information
    
    private String addressLine1;
    
    
    private String addressLine2;
    
   
    private String city;
    
    
    private String state;
    
    
    private String country;
    
    
    private String zipCode;
    
    
    private String linkedinProfile;
    
    
    private String portfolioUrl;
    
   
    private Double hourlyRate;
    
    private String availability;
    private String preferredWorkType;
    
    
    private String description;
    
    private String status ;
}
