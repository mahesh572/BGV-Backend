package com.org.bgv.vendor.dto;


import com.org.bgv.enums.VerificationExecutionStatus;
import lombok.Data;

@Data
public class UpdateExecutionStatusRequest {

    private VerificationExecutionStatus status;

    private String notes;
}