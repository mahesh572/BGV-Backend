package com.org.bgv.vendor.dto;


import java.util.List;

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
    
    private List<VerificationMethodFieldDTO> fields;
}
