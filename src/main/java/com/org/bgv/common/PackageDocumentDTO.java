package com.org.bgv.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageDocumentDTO {
    private Long id;
    private Long documentTypeId;
    private String documentName;
    private String documentCode;
    private Boolean required;
    private Integer priorityOrder;
    private Boolean selected;
}

