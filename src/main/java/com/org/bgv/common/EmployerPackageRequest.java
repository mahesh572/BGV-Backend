package com.org.bgv.common;

import lombok.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerPackageRequest {
    
    @NotNull
    private Long companyId;  
    
    @NotNull
    private Long packageId;
    
    @NotNull
    @Positive
    private Double basePrice;
    
    private List<EmployerPackageDocumentRequest> documents;
}