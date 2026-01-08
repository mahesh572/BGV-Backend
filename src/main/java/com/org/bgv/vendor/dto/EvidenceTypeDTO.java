package com.org.bgv.vendor.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvidenceTypeDTO {

    private Long id;
    private String name;     // University Verification
    private String value;    // university_verification
}