package com.org.bgv.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentTypeResponse {
    private Long docTypeId;
    private String name;
    private CheckCategoryResponse category;
    private String label;
    private Boolean isRequired;
    private String upload;
    private String code;
    private Double price;
}