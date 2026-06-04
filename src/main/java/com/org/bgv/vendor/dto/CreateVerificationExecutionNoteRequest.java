package com.org.bgv.vendor.dto;


import com.org.bgv.enums.VendorNoteType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateVerificationExecutionNoteRequest {

    private Long executionId;

    private String note;

    private VendorNoteType type = VendorNoteType.GENERAL;
}