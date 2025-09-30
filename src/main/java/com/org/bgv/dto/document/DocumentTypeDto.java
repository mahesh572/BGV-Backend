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
public class DocumentTypeDto {
    private Long typeId;
    private String typeName;
    private String typeLabel;
    private String description;
    private Boolean isRequired;
    private Integer maxFiles;
    private String customTypeName; // For "Other" category
    private List<FileDTO> files;
}