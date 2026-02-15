package com.org.bgv.common;

import java.util.List;

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
    private String name; // Aadhaar Card (optional if same as label)
    private CheckCategoryResponse category;
    private String label; // Aadhaar Card
    private Boolean isRequired;
    private String upload;
    private String code; // AADHAAR
    private Double price;
    private Integer maxFiles;
    private List<DocumentAttributeResponse> attributes;
}