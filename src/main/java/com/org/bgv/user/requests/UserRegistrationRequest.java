package com.org.bgv.user.requests;

import java.time.LocalDate;
import java.util.Set;

import com.org.bgv.user.enums.Gender;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserRegistrationRequest {

    private String email;

    private String password;

    private String firstName;

    private String lastName;

   // private Long companyId;

    private Set<String> roles;
    
    private String mobileNumber;

    private LocalDate dateOfBirth;

    private Gender gender;
    
 // Location
    private Long countryId;

    private Long stateId;

    private Long cityId;

    private String postalCode;

   // private CandidateRequest candidate;
}