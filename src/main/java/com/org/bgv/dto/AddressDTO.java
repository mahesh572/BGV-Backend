package com.org.bgv.dto;


import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.org.bgv.entity.AddressType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressDTO {
	
    private Long id;
    
    @NotNull(message = "Candidate ID is required")
    private Long candidateId;
    
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private boolean isDefault;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate currentlyResidingFrom;
    
    private boolean verified;
    private String verificationStatus = "pending";
    private String verifiedBy;
    
    private AddressType addressType;
    
    private Boolean isMyPermanentAddress;
    
    private Boolean currentlyResidingAtThisAddress;

	
}

