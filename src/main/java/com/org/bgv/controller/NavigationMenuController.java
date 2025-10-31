package com.org.bgv.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.common.navigation.CreateNavigationMenuDto;
import com.org.bgv.common.navigation.NavigationResponseDto;
import com.org.bgv.common.navigation.UpdateNavigationMenuDto;
import com.org.bgv.service.NavigationMenuService;


import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/navigation")
@CrossOrigin(origins = "*")
public class NavigationMenuController {

    private static final Logger logger = LoggerFactory.getLogger(NavigationMenuController.class);

    @Autowired
    private NavigationMenuService navigationMenuService;

    @GetMapping
    public ResponseEntity<CustomApiResponse<List<NavigationResponseDto>>> getAllNavigationMenus() {
        try {
            logger.info("navigation/getAllNavigationMenus::::::START");
            List<NavigationResponseDto> menus = navigationMenuService.getAllNavigationMenus();
            logger.info("navigation/getAllNavigationMenus::::::SUCCESS - Found {} menus", menus.size());
            return ResponseEntity.ok()
                    .body(CustomApiResponse.success("Navigation menus retrieved successfully", menus, HttpStatus.OK));
        } catch (RuntimeException e) {
            logger.error("navigation/getAllNavigationMenus::::::ERROR - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            logger.error("navigation/getAllNavigationMenus::::::ERROR - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve navigation menus:: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/active")
    public ResponseEntity<CustomApiResponse<List<NavigationResponseDto>>> getActiveNavigationMenus() {
        try {
            logger.info("navigation/getActiveNavigationMenus::::::START");
            List<NavigationResponseDto> menus = navigationMenuService.getActiveNavigationMenus();
            logger.info("navigation/getActiveNavigationMenus::::::SUCCESS - Found {} active menus", menus.size());
            return ResponseEntity.ok()
                    .body(CustomApiResponse.success("Active navigation menus retrieved successfully", menus, HttpStatus.OK));
        } catch (RuntimeException e) {
            logger.error("navigation/getActiveNavigationMenus::::::ERROR - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            logger.error("navigation/getActiveNavigationMenus::::::ERROR - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve active navigation menus: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<CustomApiResponse<List<NavigationResponseDto>>> getNavigationByRole(@PathVariable String role) {
        try {
            logger.info("navigation/getNavigationByRole::::::START - Role: {}", role);
            List<NavigationResponseDto> menus = navigationMenuService.getNavigationByRole(role);
            logger.info("navigation/getNavigationByRole::::::SUCCESS - Found {} menus for role: {}", menus.size(), role);
            return ResponseEntity.ok()
                    .body(CustomApiResponse.success("Navigation menus retrieved successfully for role: " + role, menus, HttpStatus.OK));
        } catch (RuntimeException e) {
            logger.error("navigation/getNavigationByRole::::::ERROR - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            logger.error("navigation/getNavigationByRole::::::ERROR - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve navigation menus for role: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse<NavigationResponseDto>> getNavigationMenuById(@PathVariable Long id) {
        try {
            logger.info("navigation/getNavigationMenuById::::::START - ID: {}", id);
            NavigationResponseDto menu = navigationMenuService.getNavigationMenuById(id);
            logger.info("navigation/getNavigationMenuById::::::SUCCESS - Menu found: {}", menu.getName());
            return ResponseEntity.ok()
                    .body(CustomApiResponse.success("Navigation menu retrieved successfully", menu, HttpStatus.OK));
        } catch (RuntimeException e) {
            logger.error("navigation/getNavigationMenuById::::::ERROR - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            logger.error("navigation/getNavigationMenuById::::::ERROR - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve navigation menu: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping
    public ResponseEntity<CustomApiResponse<NavigationResponseDto>> createNavigationMenu(
           @RequestBody CreateNavigationMenuDto createDto) {
        try {
            logger.info("navigation/createNavigationMenu::::::{}", createDto);
            NavigationResponseDto createdMenu = navigationMenuService.createNavigationMenu(createDto);
            logger.info("navigation/createNavigationMenu::::::SUCCESS - Created menu: {}", createdMenu.getName());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CustomApiResponse.success("Navigation menu created successfully", createdMenu, HttpStatus.CREATED));
        } catch (RuntimeException e) {
            logger.error("navigation/createNavigationMenu::::::ERROR - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            logger.error("navigation/createNavigationMenu::::::ERROR - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to create navigation menu: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomApiResponse<NavigationResponseDto>> updateNavigationMenu(
            @PathVariable Long id,
            @Valid @RequestBody UpdateNavigationMenuDto updateDto) {
        try {
            logger.info("navigation/updateNavigationMenu::::::ID: {}, UpdateDTO: {}", id, updateDto);
            NavigationResponseDto updatedMenu = navigationMenuService.updateNavigationMenu(id, updateDto);
            logger.info("navigation/updateNavigationMenu::::::SUCCESS - Updated menu: {}", updatedMenu.getName());
            return ResponseEntity.ok()
                    .body(CustomApiResponse.success("Navigation menu updated successfully", updatedMenu, HttpStatus.OK));
        } catch (RuntimeException e) {
            logger.error("navigation/updateNavigationMenu::::::ERROR - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            logger.error("navigation/updateNavigationMenu::::::ERROR - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to update navigation menu: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse<Void>> deleteNavigationMenu(@PathVariable Long id) {
        try {
            logger.info("navigation/deleteNavigationMenu::::::START - ID: {}", id);
            navigationMenuService.deleteNavigationMenu(id);
            logger.info("navigation/deleteNavigationMenu::::::SUCCESS - Deleted menu with ID: {}", id);
            return ResponseEntity.ok()
                    .body(CustomApiResponse.success("Navigation menu deleted successfully", null, HttpStatus.OK));
        } catch (RuntimeException e) {
            logger.error("navigation/deleteNavigationMenu::::::ERROR - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            logger.error("navigation/deleteNavigationMenu::::::ERROR - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to delete navigation menu: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<CustomApiResponse<Void>> toggleNavigationMenuStatus(@PathVariable Long id) {
        try {
            logger.info("navigation/toggleNavigationMenuStatus::::::START - ID: {}", id);
            navigationMenuService.toggleNavigationMenuStatus(id);
            logger.info("navigation/toggleNavigationMenuStatus::::::SUCCESS - Toggled status for menu ID: {}", id);
            return ResponseEntity.ok()
                    .body(CustomApiResponse.success("Navigation menu status toggled successfully", null, HttpStatus.OK));
        } catch (RuntimeException e) {
            logger.error("navigation/toggleNavigationMenuStatus::::::ERROR - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            logger.error("navigation/toggleNavigationMenuStatus::::::ERROR - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to toggle navigation menu status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}