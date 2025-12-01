package com.org.bgv.common;

import java.time.LocalDateTime;
import java.util.List;

import com.org.bgv.entity.BgvPackage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerPackageResponse {
    
    private Long id;
    private Long companyId;  
    private PackageInfo bgvPackage;
    private Double basePrice;
    private Double addonPrice;
    private Double totalPrice;
    private String status;
    private LocalDateTime createdAt;
    private List<EmployerPackageDocumentResponse> documents;
}

