package com.org.bgv.vendor.dto;


import com.org.bgv.enums.VerificationExecutionStatus;
import lombok.Data;

@Data
public class UpdateExecutionStatusRequest {

    private VerificationExecutionStatus status;
    
    private String outcome;

    private String notes;
    
    private Double latitude;

    private Double longitude;

    private Double accuracy;

    private String address;
}