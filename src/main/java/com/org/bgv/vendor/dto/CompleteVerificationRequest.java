package com.org.bgv.vendor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompleteVerificationRequest {
	private String finalStatus;
    private String summary;
}
