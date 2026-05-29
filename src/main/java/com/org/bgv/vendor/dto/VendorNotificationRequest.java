package com.org.bgv.vendor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorNotificationRequest {

    private Long caseId;

    private Long checkId;
}
