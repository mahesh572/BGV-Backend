package com.org.bgv.common;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentAttributeResponse {

    private Long attributeId;
    private String code;        // FRONT, BACK, PAGE_1
    private String label;       // Front Side
    private Boolean mandatory;  // true / false
    private Integer maxFiles;
    private Integer sequence;
}

