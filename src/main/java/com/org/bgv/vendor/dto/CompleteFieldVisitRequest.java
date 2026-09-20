package com.org.bgv.vendor.dto;


import com.org.bgv.enums.VerificationOutcome;

import lombok.Data;

@Data
public class CompleteFieldVisitRequest {

  //  private String outcome;
	
	private VerificationOutcome outcome;

    private String remarks;

    private Double latitude;

    private Double longitude;

    private String address;
}
