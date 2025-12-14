package com.org.bgv.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentSelectionDto {
    private Long documentTypeId;
    private String documentName;
    private String documentCode;
    private String documentLabel;
    private Double price;
    private boolean isSelected;
    private boolean isDisabled; // Already selected in package
    private Boolean includedInBase;
    private String selectionType;
    private boolean isRequired;
    private Long employerPackageDocumentId; // For removing add-ons
}