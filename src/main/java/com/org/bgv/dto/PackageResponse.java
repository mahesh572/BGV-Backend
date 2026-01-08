package com.org.bgv.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageResponse {
    private Long pId;
    private String name;
    private String code;
    private String description;
    private Boolean customizable;
    private Double basePrice;
    private Boolean isActive;
    private List<PackageCheckCategoryResponse> packageCheckCategories;
}
