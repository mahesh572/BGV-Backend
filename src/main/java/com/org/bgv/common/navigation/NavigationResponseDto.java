package com.org.bgv.common.navigation;


import java.util.List;
import java.util.stream.Collectors;

import com.org.bgv.entity.NavigationMenu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class NavigationResponseDto {

    private Long id;
    private String name;
    private String href;
    private String icon;
    private String color;
    private NavigationType type;
    private PortalType portal;
    private String label;
    private List<String> permissions;
    private Integer order;
    private Boolean isActive;
    private List<NavigationResponseDto> children;
    private Long parentId;
    private String basePath;

    public NavigationResponseDto(NavigationMenu menu) {
    	
    	log.info("menu.getName()::::::::::{}",menu.getName());
    	
        this.id = menu.getId();
        this.name = menu.getName();
        this.href = menu.getHref();
        this.icon = menu.getIcon();
        this.color = menu.getColor();
        this.type = menu.getType();
        this.portal = menu.getPortal();
        this.label = menu.getLabel();
        this.permissions = menu.getPermissions();
        this.order = menu.getOrder();
        this.isActive = menu.getIsActive();
        this.basePath=menu.getBasePath();
        
        this.parentId = menu.getParent() != null ? menu.getParent().getId() : null;
        this.portal = menu.getPortal();

        log.info("menu.getName():::::menu.getChildren()::::{}",menu.getChildren()!=null);
        
        if (menu.getChildren() != null) {
            this.children = menu.getChildren()
                .stream()
                .map(NavigationResponseDto::new)
                .toList();
        }
    }
}
