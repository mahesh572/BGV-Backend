package com.org.bgv.vendor.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationMethodDTO {

    private Long methodId;

    private String code;

    private String name;

    private String description;

    private Boolean mandatory;
}
