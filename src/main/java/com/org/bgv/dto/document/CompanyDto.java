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
public class CompanyDto {
	
    private Long companyId;
    private String companyName;
   
    private List<DocumentTypeDto> documentTypes;
}