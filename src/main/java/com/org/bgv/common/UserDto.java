package com.org.bgv.common;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.org.bgv.entity.UserType;
import com.org.bgv.user.enums.Gender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long userId;
    private String email;
    private UserType userType;
    private String firstName;
    private String lastName;
    private String fullName;
  //  private String name; // firstName + lastName
    private String phoneNumber;
    private Boolean isActive;
    private Boolean isVerified;
    private String profilePictureUrl;
    private Gender gender;
    private String status;
    private LocalDate dateOfBirth;
    private Long profileId;
    private Long companyId;
    private List<String> roles;
    private Boolean hasConsentProvided;
    private Long candidateId;
    private Boolean passwordResetrequired;
    private String landingPage;
  //  private Long vendorId;
  //  private Long caseId;
    
    public String getFullName() {
        return Stream.of(firstName, lastName)
                .filter(Objects::nonNull)
                .filter(s -> !s.isBlank())
                .collect(Collectors.joining(" "));
    }
    
}
