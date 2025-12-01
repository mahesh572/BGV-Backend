package com.org.bgv.common;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateCaseRequest {
    
    @NotNull
    private Long candidateId;
    
    @NotNull
    private Long companyId;
    
    @NotNull
    private Long employerPackageId;
    
    private List<Long> selectedAddonDocumentIds;
}