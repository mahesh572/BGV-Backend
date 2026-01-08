package com.org.bgv.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageDTO {
   
	private Long packageId;
    
    @NotBlank(message = "Package name is required")
    private String name;
    
    @NotBlank(message = "Package code is required")
    private String code;
    
    private String description;
    
    @NotNull(message = "Customizable flag is required")
    private Boolean customizable;
    
    private Double basePrice;
    
    @NotNull(message = "Active status is required")
    private Boolean isActive;
    
    private Double price;
    
    private Boolean isAssigned;
    
    private List<PackageCategoryDTO> categories;
}