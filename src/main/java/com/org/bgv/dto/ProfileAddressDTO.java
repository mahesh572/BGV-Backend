package com.org.bgv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileAddressDTO {
    private Long profileAddressId;
    private String addressLine1;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private Boolean curResiding;
    private LocalDate curResidingFrom;
    private Boolean isPermanentAddress;
    private Long profile_id;
}