package com.org.bgv.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class BGVCategoryResponse {
    private Long categoryId;
    private String name;
    private String label;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CheckTypeResponse> checkTypes;
}