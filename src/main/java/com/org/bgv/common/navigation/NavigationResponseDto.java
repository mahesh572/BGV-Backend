package com.org.bgv.common.navigation;


import java.util.List;
import java.util.stream.Collectors;

import com.org.bgv.entity.NavigationMenu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NavigationResponseDto {
    private Long id;
    private String name;
    private String href;
    private String icon;
    private String color;
    private String type;
    private List<String> permissions;
    private Integer order;
    private Boolean isActive;
    private String label;
    private List<NavigationResponseDto> children;

    
    public NavigationResponseDto(NavigationMenu menu) {
        this.id = menu.getId();
        this.name = menu.getName();
        this.href = menu.getHref();
        this.icon = menu.getIcon();
        this.color = menu.getColor();
        this.type = menu.getType();
        this.label = menu.getLabel();
        this.permissions = menu.getPermissions();
        this.order = menu.getOrder();
        this.isActive = menu.getIsActive();
        
        if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
            this.children = menu.getChildren().stream()
                    .map(NavigationResponseDto::new)
                    .collect(Collectors.toList());
        }
    }

    
}