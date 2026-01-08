package com.org.bgv.dto.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.org.bgv.dto.FieldDTO;
import com.org.bgv.dto.UploadRuleDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentTypeDto {
	
	private Long id;
    private Long typeId;
    private String typeName;
    private String typeLabel;
    private String description;
    private Boolean isRequired;
    private Integer maxFiles;
    private String customTypeName; // For "Other" category
    private List<FileDTO> files;
    
    private List<FieldDTO> fields;
   // private UploadRuleDTO upload;
    private boolean error;
    private String errorMessage;
}