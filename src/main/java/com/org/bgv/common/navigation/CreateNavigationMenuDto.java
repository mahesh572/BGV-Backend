package com.org.bgv.common.navigation;

import java.util.List;

import com.org.bgv.entity.NavigationMenu;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateNavigationMenuDto {
    
   
    private String name;
    
    private String href;
    
   
    private String icon;
    
    private String color;
    
   
    private String type;
    
    private List<String> permissions;
    
    private Integer order = 0;
    
    private Boolean isActive = true;
    
    private String parentId;
    
    private List<CreateNavigationMenuDto> children;
    
    private String createdBy;

}
