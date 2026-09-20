package com.org.bgv.common;

import java.math.BigDecimal;
import java.util.List;

import com.org.bgv.enums.CaseSource;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationCaseRequest {
    
	@NotNull
    private Long candidateId;
    
    @NotNull
    private Long companyId;
    
    private Long packageId;
    
    private Long employerPackageId;
    
   // @NotNull
    private BigDecimal totalPrice;
    
    private List<CategoryCase> categories;
    
    private CaseSource source;
}