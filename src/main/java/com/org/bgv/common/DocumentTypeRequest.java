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
public class DocumentTypeRequest {
    private String name;
    private Long categoryId;
    private String label;
    private Boolean isRequired;
    private String upload;
    private String code;
    private Double price;
    
    List<DocumentAttributeRequest> attributes;
}