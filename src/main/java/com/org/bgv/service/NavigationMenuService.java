package com.org.bgv.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.org.bgv.common.navigation.CreateNavigationMenuDto;
import com.org.bgv.common.navigation.NavigationResponseDto;
import com.org.bgv.common.navigation.UpdateNavigationMenuDto;
import com.org.bgv.entity.NavigationMenu;
import com.org.bgv.repository.NavigationMenuRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Transactional
@Service
public class NavigationMenuService {

    @Autowired
    private NavigationMenuRepository navigationMenuRepository;

    public List<NavigationResponseDto> getAllNavigationMenus() {
        List<NavigationMenu> rootMenus = navigationMenuRepository.findByParentIsNullOrderByOrderAsc();
        return rootMenus.stream()
                .map(NavigationResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<NavigationResponseDto> getActiveNavigationMenus() {
        List<NavigationMenu> rootMenus = navigationMenuRepository.findByIsActiveTrueAndParentIsNullOrderByOrderAsc();
        return rootMenus.stream()
                .map(NavigationResponseDto::new)
                .collect(Collectors.toList());
    }

    public List<NavigationResponseDto> getNavigationByRole(String role) {
        List<NavigationMenu> rootMenus = navigationMenuRepository.findRootMenusByRole(role);
        return rootMenus.stream()
                .map(NavigationResponseDto::new)
                .collect(Collectors.toList());
    }

    public NavigationResponseDto getNavigationMenuById(Long id) {
        NavigationMenu menu = navigationMenuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Navigation menu not found with id: " + id));
        return new NavigationResponseDto(menu);
    }

    public NavigationResponseDto createNavigationMenu(CreateNavigationMenuDto createDto) {
        // Check for duplicate name
        if (navigationMenuRepository.existsByNameAndParentId(createDto.getName(), createDto.getParentId())) {
            throw new IllegalArgumentException("Navigation menu with name '" + createDto.getName() + "' already exists");
        }

        
        NavigationMenu menu = new NavigationMenu();
        mapCreateDtoToEntity(createDto, menu);

        // Set parent if provided
        if (createDto.getParentId() != null && createDto.getParentId() !=0) {
            NavigationMenu parent = navigationMenuRepository.findById(createDto.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent menu not found with id: " + createDto.getParentId()));
            menu.setParent(parent);
        }

        NavigationMenu savedMenu = navigationMenuRepository.save(menu);
        
        // Handle children recursively if provided
        if (createDto.getChildren() != null && !createDto.getChildren().isEmpty()) {
            for (CreateNavigationMenuDto childDto : createDto.getChildren()) {
                childDto.setParentId(savedMenu.getId());
                createNavigationMenu(childDto);
            }
        }

        return new NavigationResponseDto(savedMenu);
    }

    public NavigationResponseDto updateNavigationMenu(Long id, UpdateNavigationMenuDto updateDto) {
        NavigationMenu menu = navigationMenuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Navigation menu not found with id: " + id));

        // Check for duplicate name (excluding current menu)
        if (updateDto.getName() != null && 
            !menu.getName().equals(updateDto.getName()) &&
            navigationMenuRepository.existsByNameAndParentId(updateDto.getName(), menu.getParent() != null ? menu.getParent().getId() : null)) {
            throw new IllegalArgumentException("Navigation menu with name '" + updateDto.getName() + "' already exists");
        }

        mapUpdateDtoToEntity(updateDto, menu);

        // Update parent if provided
        if (updateDto.getParentId() != null) {
            NavigationMenu parent = navigationMenuRepository.findById(updateDto.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Parent menu not found with id: " + updateDto.getParentId()));
            menu.setParent(parent);
        }

        NavigationMenu updatedMenu = navigationMenuRepository.save(menu);
        return new NavigationResponseDto(updatedMenu);
    }

    public void deleteNavigationMenu(Long id) {
        NavigationMenu menu = navigationMenuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Navigation menu not found with id: " + id));
        
        // If it's a parent menu, delete children first (cascade)
        if (menu.getChildren() != null && !menu.getChildren().isEmpty()) {
            navigationMenuRepository.deleteAll(menu.getChildren());
        }
        
        navigationMenuRepository.delete(menu);
    }

    public void toggleNavigationMenuStatus(Long id) {
        NavigationMenu menu = navigationMenuRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Navigation menu not found with id: " + id));
        
        menu.setIsActive(!menu.getIsActive());
        navigationMenuRepository.save(menu);
    }

    private void mapCreateDtoToEntity(CreateNavigationMenuDto dto, NavigationMenu entity) {
        entity.setName(dto.getName());
        entity.setHref(dto.getHref());
        entity.setIcon(dto.getIcon());
        entity.setColor(dto.getColor());
        entity.setType(dto.getType().toUpperCase());
        entity.setLabel(dto.getLabel());
        entity.setPermissions(dto.getPermissions());
        entity.setOrder(dto.getOrder());
        entity.setIsActive(dto.getIsActive());
        entity.setCreatedBy(dto.getCreatedBy());
    }

    private void mapUpdateDtoToEntity(UpdateNavigationMenuDto dto, NavigationMenu entity) {
        if (dto.getName() != null) entity.setName(dto.getName());
        if (dto.getHref() != null) entity.setHref(dto.getHref());
        if (dto.getIcon() != null) entity.setIcon(dto.getIcon());
        if (dto.getColor() != null) entity.setColor(dto.getColor());
        if (dto.getPermissions() != null) entity.setPermissions(dto.getPermissions());
        if (dto.getOrder() != null) entity.setOrder(dto.getOrder());
        if (dto.getIsActive() != null) entity.setIsActive(dto.getIsActive());
    }
}