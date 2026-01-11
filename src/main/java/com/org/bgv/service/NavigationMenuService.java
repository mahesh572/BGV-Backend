package com.org.bgv.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.candidate.repository.CandidateRepository;
import com.org.bgv.common.VerificationCaseResponse;
import com.org.bgv.common.navigation.CreateNavigationMenuDto;
import com.org.bgv.common.navigation.NavigationResponseDto;
import com.org.bgv.common.navigation.UpdateNavigationMenuDto;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.NavigationMenu;
import com.org.bgv.entity.User;
import com.org.bgv.entity.UserType;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.repository.NavigationMenuRepository;
import com.org.bgv.repository.UserRepository;
import com.org.bgv.repository.VerificationCaseRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Transactional
@Service
@AllArgsConstructor
@Slf4j
public class NavigationMenuService {
	
	private final UserRepository userRepository;
	// private final VerificationCaseRepository
	private final CandidateRepository candidateRepository;
	private final VerificationCaseService verificationCaseService;
	private final VerificationCaseRepository verificationCaseRepository;

    @Autowired
    private NavigationMenuRepository navigationMenuRepository;

    public List<NavigationResponseDto> getAllNavigationMenus(String action) {
        try {
            List<NavigationMenu> rootMenus =
                    navigationMenuRepository.findByParentIsNullOrderByOrderAsc();

            List<NavigationMenu> resultMenus;

            if ("ALL".equalsIgnoreCase(action)) {
                resultMenus = rootMenus;
            } else {
                List<String> authorities = SecurityUtils.getCurrentUserAuthorities();

                resultMenus = rootMenus.stream()
                        .map(menu -> filterMenuByPermissions(menu, authorities))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                applyCandidateSpecificNavigation(resultMenus);
            }

            return resultMenus.stream()
                    .map(NavigationResponseDto::new)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            log.error("Error while fetching navigation menus", ex);
            throw new RuntimeException("Unable to load navigation menus");
        }
    }
    private void applyCandidateSpecificNavigation(List<NavigationMenu> resultMenus) {
        try {
            Long userId = SecurityUtils.getCurrentCustomUserDetails().getUserId();

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));

            if (!UserType.CANDIDATE.name().equalsIgnoreCase(user.getUserType())) {
                return;
            }

            Candidate candidate = candidateRepository.findByUserUserId(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Candidate not found"));

            if (!UserType.COMPANY.name().equalsIgnoreCase(candidate.getSourceType())) {
                return;
            }

            Set<String> allowedMenus = new HashSet<>(Set.of(
                    "DashBoard", "Basic Details", "Documents",
                    "Education", "Work Experience", "Address",
                    "verification", "Cases"
            ));

            Set<String> checkTypes =
                    verificationCaseRepository.findByCandidateId(candidate.getCandidateId())
                            .stream()
                            .flatMap(vc -> vc.getCaseChecks().stream())
                            .map(check -> check.getCategory() != null
                                    ? check.getCategory().getName()
                                    : null)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toSet());

            allowedMenus.addAll(checkTypes);

            resultMenus.removeIf(menu -> !allowedMenus.contains(menu.getName()));
            

        } catch (EntityNotFoundException ex) {
            log.warn("Candidate navigation skipped: {}", ex.getMessage());
        } catch (Exception ex) {
        	
            log.error("Error applying candidate-specific navigation", ex);
            throw new RuntimeException("Navigation resolution failed");
        }
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
        try {
            if (navigationMenuRepository.existsByNameAndParentId(
                    createDto.getName(), createDto.getParentId())) {
                throw new IllegalArgumentException(
                        "Navigation menu already exists: " + createDto.getName());
            }

            NavigationMenu menu = new NavigationMenu();
            mapCreateDtoToEntity(createDto, menu);

            if (createDto.getParentId() != null && createDto.getParentId() != 0) {
                NavigationMenu parent = navigationMenuRepository.findById(createDto.getParentId())
                        .orElseThrow(() -> new EntityNotFoundException("Parent menu not found"));
                menu.setParent(parent);
            }

            NavigationMenu saved = navigationMenuRepository.save(menu);

            if (createDto.getChildren() != null) {
                for (CreateNavigationMenuDto child : createDto.getChildren()) {
                    child.setParentId(saved.getId());
                    createNavigationMenu(child);
                }
            }

            return new NavigationResponseDto(saved);

        } catch (Exception ex) {
            log.error("Failed to create navigation menu: {}", createDto.getName(), ex);
            throw new RuntimeException("Navigation menu creation failed");
        }
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
    
    private NavigationMenu filterMenuByPermissions(NavigationMenu menu, List<String> authorities) {
        // Check if this menu is allowed
        boolean hasAccess = menu.getPermissions().isEmpty() ||
                menu.getPermissions().stream().anyMatch(authorities::contains);

        // Recursively filter children
        List<NavigationMenu> filteredChildren = menu.getChildren().stream()
                .map(child -> filterMenuByPermissions(child, authorities))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // If menu has access or has accessible children, include it
        if (hasAccess || !filteredChildren.isEmpty()) {
            NavigationMenu filtered = new NavigationMenu();
            filtered.setId(menu.getId());
            filtered.setName(menu.getName());
            filtered.setHref(menu.getHref());
            filtered.setIcon(menu.getIcon());
            filtered.setColor(menu.getColor());
            filtered.setLabel(menu.getLabel());
            filtered.setType(menu.getType());
            filtered.setOrder(menu.getOrder());
            filtered.setIsActive(menu.getIsActive());
            filtered.setPermissions(menu.getPermissions());
            filtered.setChildren(filteredChildren);
            return filtered;
        }

        return null;
    }
}