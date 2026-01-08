package com.org.bgv.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PackageCheckCategoryResponse {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String rulesData;
}
