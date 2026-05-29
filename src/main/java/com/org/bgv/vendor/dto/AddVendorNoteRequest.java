package com.org.bgv.vendor.dto;

import com.org.bgv.enums.VendorNoteType;

import lombok.Data;

@Data
public class AddVendorNoteRequest {

    private String content;

    private VendorNoteType noteType = VendorNoteType.INTERNAL;
}