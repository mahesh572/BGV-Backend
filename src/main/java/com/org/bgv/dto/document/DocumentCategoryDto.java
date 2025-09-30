package com.org.bgv.dto.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentCategoryDto {
    private Long categoryId;
    private String categoryName;
    private String categoryLabel;
    private Boolean isRequired;
    private Integer maxDocuments;
    private List<String> allowedFileTypes;
    private Integer maxFileSize; // in MB
    
    // For categories that don't have companies (Identity, Education, Other)
    private List<DocumentTypeDto> documentTypes;
    
    // Specifically for Work Experience category
    private List<CompanyDto> companies;
    private List<EducationDTO> education;
}