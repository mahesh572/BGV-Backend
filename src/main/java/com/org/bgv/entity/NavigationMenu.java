package com.org.bgv.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.org.bgv.common.navigation.NavigationType;
import com.org.bgv.common.navigation.PortalType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "navigation_menus")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NavigationMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(name = "base_path")
    private String basePath;

    // Only for LINK
    @Column(length = 255)
    private String href;

    @Column(length = 100)
    private String icon;

    @Column(length = 50)
    private String color;

    @Column(nullable = false)
    private String label;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NavigationType type;
    

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PortalType portal;

    @ElementCollection
    @CollectionTable(
        name = "menu_permissions",
        joinColumns = @JoinColumn(name = "menu_id")
    )
    @Column(name = "permission")
    private List<String> permissions = new ArrayList<>();

    @Column(name = "menu_order", nullable = false)
    private Integer order = 0;

    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column
    private Boolean hidden = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonBackReference
    private NavigationMenu parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<NavigationMenu> children = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
}
