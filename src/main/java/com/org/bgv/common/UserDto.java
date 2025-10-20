package com.org.bgv.common;

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
    private String userType;
    private String firstName;
    private String lastName;
    private String name; // firstName + lastName
    private String phoneNumber;
    private Boolean isActive;
    private Boolean isVerified;
    private String profilePictureUrl;
    private String gender;
    private String status;
    private String dateOfBirth;
}
