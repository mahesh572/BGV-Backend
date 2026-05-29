package com.org.bgv.common;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerPackageDocumentResponse {
    
    private Long id;
    private CategoryInfo checkCategory;
    private DocumentTypeInfo documentType;
    private BigDecimal addonPrice;
    private Boolean includedInBase;
    private String selectionType;
}