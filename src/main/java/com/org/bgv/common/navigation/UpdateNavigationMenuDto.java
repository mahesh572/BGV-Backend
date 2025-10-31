package com.org.bgv.common.navigation;



import lombok.Builder;
import lombok.Data;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

@Builder
@Data
public class UpdateNavigationMenuDto {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    private String href;
    
    @NotBlank(message = "Icon is required")
    private String icon;
    
    private String color;
    
    private List<String> permissions;
    
    private Integer order;
    
    private Boolean isActive;
    
    private Long parentId;

    
}
