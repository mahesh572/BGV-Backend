package com.org.bgv.common;

import com.org.bgv.constants.SelectionType;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerPackageDocumentRequest {
    
    @NotNull
    private Long checkCategoryId;
    
    @NotNull
    private Long documentTypeId;
    
    private Double addonPrice;
    
    private Boolean includedInBase;
    
    private SelectionType selectionType;
}
