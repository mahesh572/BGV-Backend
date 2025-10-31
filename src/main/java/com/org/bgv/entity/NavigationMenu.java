package com.org.bgv.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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

@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "navigation_menus")
public class NavigationMenu {
 
 

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(name = "name", nullable = false, length = 100)
 private String name;

 @Column(name = "href", length = 255)
 private String href;

 @Column(name = "icon")
 private String icon;

 @Column(name = "color", length = 50)
 private String color;

 @Column(name = "label", nullable = false)
 private String label;
 
 @Column(name = "type", nullable = false)
 private String type;

 @ElementCollection
 @CollectionTable(name = "menu_permissions", joinColumns = @JoinColumn(name = "menu_id"))
 @Column(name = "permission")
 private List<String> permissions = new ArrayList<>();

 @Column(name = "menu_order", nullable = false)
 private Integer order = 0;

 @Column(name = "is_active", nullable = false)
 private Boolean isActive = true;

 @ManyToOne(fetch = FetchType.LAZY)
 @JoinColumn(name = "parent_id")
 @JsonBackReference
 private NavigationMenu parent;

 @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
 @JsonManagedReference
 private List<NavigationMenu> children = new ArrayList<>();

 @Column(name = "created_at", updatable = false)
 private LocalDateTime createdAt;

 @Column(name = "updated_at")
 private LocalDateTime updatedAt;

 @Column(name = "created_by", length = 100)
 private String createdBy;

 

 // Helper methods
 public void addChild(NavigationMenu child) {
     children.add(child);
     child.setParent(this);
 }

 public void removeChild(NavigationMenu child) {
     children.remove(child);
     child.setParent(null);
 }
}