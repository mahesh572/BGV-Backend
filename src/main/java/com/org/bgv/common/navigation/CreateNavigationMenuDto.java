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
@NoArgsConstructor
@AllArgsConstructor
public class CreateNavigationMenuDto {

    @NotBlank
    private String name;

    // Required only for LINK
    private String href;

    private String icon;
    private String color;

    @NotBlank
    private String label;

    @NotNull
    private NavigationType type;

    @NotNull
    private PortalType portal;

    private List<String> permissions;
    private Integer order = 0;
    private Boolean isActive = true;

    private Long parentId;
    private List<CreateNavigationMenuDto> children;

    private String createdBy;
}

