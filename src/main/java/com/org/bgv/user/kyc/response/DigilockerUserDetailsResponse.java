package com.org.bgv.user.kyc.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DigilockerUserDetailsResponse {

    private String name;

    private String dob;

    private String gender;

    private String eaadhaar;

    private String mobile;
}
