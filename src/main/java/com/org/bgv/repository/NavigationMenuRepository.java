package com.org.bgv.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.NavigationMenu;

@Repository
public interface NavigationMenuRepository extends JpaRepository<NavigationMenu, Long> {
    
    List<NavigationMenu> findByParentIsNullOrderByOrderAsc();
    
    List<NavigationMenu> findByIsActiveTrueAndParentIsNullOrderByOrderAsc();
    
    List<NavigationMenu> findByParentIdOrderByOrderAsc(Long parentId);
    
    List<NavigationMenu> findByIsActiveTrueAndParentIdOrderByOrderAsc(Long parentId);
    
    Optional<NavigationMenu> findByName(String name);
    
    boolean existsByNameAndParentId(String name, Long parentId);
    
    @Query("SELECT m FROM NavigationMenu m WHERE m.isActive = true AND :role MEMBER OF m.permissions ORDER BY m.order ASC")
    List<NavigationMenu> findActiveByRole(@Param("role") String role);
    
    @Query("SELECT m FROM NavigationMenu m WHERE m.parent IS NULL AND m.isActive = true AND :role MEMBER OF m.permissions ORDER BY m.order ASC")
    List<NavigationMenu> findRootMenusByRole(@Param("role") String role);
}
