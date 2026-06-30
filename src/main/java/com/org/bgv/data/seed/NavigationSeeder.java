package com.org.bgv.data.seed;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.org.bgv.common.navigation.NavigationType;
import com.org.bgv.common.navigation.PortalType;
import com.org.bgv.entity.NavigationMenu;
import com.org.bgv.repository.NavigationMenuRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class NavigationSeeder {
	
	 @Value("${app.seed.enabled:false}")
	    private boolean seedEnabled;

    private final NavigationMenuRepository navigationMenuRepository;

    @Bean
    CommandLineRunner seedVendorNavigation() {
    	
    	
    	
        return args -> {

            // Dashboard
            createOrUpdateMenu(
                    "Dashboard",
                    "/vendor/dashboard",
                    "dashboard",
                    "Dashboard",
                    NavigationType.LINK,
                    PortalType.VENDOR,
                    null,
                    1,
                    List.of("Vendor Administrator", "Vendor Agent"));

            // Users
            NavigationMenu users = createOrUpdateMenu(
                    "Users",
                    "/vendor/users",
                    "users",
                    "Users",
                    NavigationType.SECTION,
                    PortalType.VENDOR,
                    null,
                    2,
                    List.of("Vendor Administrator"));

            createOrUpdateMenu(
                    "Create Vendor User",
                    "/vendor/users/add-new-user",
                    "person_add",
                    "Add New User",
                    NavigationType.LINK,
                    PortalType.VENDOR,
                    users,
                    1,
                    List.of("Vendor Administrator"));

            createOrUpdateMenu(
                    "All Vendor Users",
                    "/vendor/users/list",
                    "groups",
                    "All Vendor Users",
                    NavigationType.LINK,
                    PortalType.VENDOR,
                    users,
                    2,
                    List.of("Vendor Administrator"));

            // Checks
            NavigationMenu checks = createOrUpdateMenu(
                    "Checks",
                    "/vendor/checks",
                    "assignment",
                    "Checks",
                    NavigationType.SECTION,
                    PortalType.VENDOR,
                    null,
                    3,
                    List.of("Vendor Administrator", "Vendor Agent"));

            createOrUpdateMenu(
                    "Assigned Checks",
                    "/vendor/checks/assigned",
                    "assignment_ind",
                    "Assigned Checks",
                    NavigationType.LINK,
                    PortalType.VENDOR,
                    checks,
                    1,
                    List.of("Vendor Administrator", "Vendor Agent"));
            	
            // Field Visits
            NavigationMenu fieldVisits = createOrUpdateMenu(
                    "Field Visits",
                    "/vendor/field-agent/dashboard",
                    "location_on",
                    "DashBoard",
                    NavigationType.LINK,
                    PortalType.VENDOR,
                    null,
                    4,
                    List.of("Field Agent"));
            
            /*
            NavigationMenu fieldVisits = createOrUpdateMenu(
                    "Field Visits",
                    "/vendor/field-visits",
                    "location_on",
                    "Field Visits",
                    NavigationType.SECTION,
                    PortalType.VENDOR,
                    null,
                    4,
                    List.of("Vendor Administrator", "Vendor Agent", "Field Agent"));

            createOrUpdateMenu(
                    "Scheduled Visits",
                    "/vendor/field-visits/scheduled",
                    "event",
                    "Scheduled Visits",
                    NavigationType.LINK,
                    PortalType.VENDOR,
                    fieldVisits,
                    1,
                    List.of("Vendor Administrator", "Vendor Agent", "Field Agent"));

            createOrUpdateMenu(
                    "Active Visits",
                    "/vendor/field-visits/active",
                    "navigation",
                    "Active Visits",
                    NavigationType.LINK,
                    PortalType.VENDOR,
                    fieldVisits,
                    2,
                    List.of("Vendor Administrator", "Vendor Agent", "Field Agent"));

            // Evidence
            NavigationMenu evidence = createOrUpdateMenu(
                    "Evidence",
                    "/vendor/evidence",
                    "photo_camera",
                    "Evidence",
                    NavigationType.SECTION,
                    PortalType.VENDOR,
                    null,
                    5,
                    List.of("Vendor Administrator", "Vendor Agent"));

            createOrUpdateMenu(
                    "Photos",
                    "/vendor/evidence/photos",
                    "image",
                    "Photos",
                    NavigationType.LINK,
                    PortalType.VENDOR,
                    evidence,
                    1,
                    List.of("Vendor Administrator", "Vendor Agent"));

            createOrUpdateMenu(
                    "Documents",
                    "/vendor/evidence/documents",
                    "description",
                    "Documents",
                    NavigationType.LINK,
                    PortalType.VENDOR,
                    evidence,
                    2,
                    List.of("Vendor Administrator", "Vendor Agent"));

            // Activity
            NavigationMenu activity = createOrUpdateMenu(
                    "Activity",
                    "/vendor/activity",
                    "history",
                    "Activity",
                    NavigationType.SECTION,
                    PortalType.VENDOR,
                    null,
                    6,
                    List.of("Vendor Administrator"));

            createOrUpdateMenu(
                    "Timeline",
                    "/vendor/activity/timeline",
                    "timeline",
                    "Timeline",
                    NavigationType.LINK,
                    PortalType.VENDOR,
                    activity,
                    1,
                    List.of("Vendor Administrator"));

            createOrUpdateMenu(
                    "Audit Logs",
                    "/vendor/activity/audit",
                    "fact_check",
                    "Audit Logs",
                    NavigationType.LINK,
                    PortalType.VENDOR,
                    activity,
                    2,
                    List.of("Vendor Administrator"));

            // Reports
            createOrUpdateMenu(
                    "Reports",
                    "/vendor/reports",
                    "analytics",
                    "Reports",
                    NavigationType.LINK,
                    PortalType.VENDOR,
                    null,
                    7,
                    List.of("Vendor Administrator"));

            // Settings
            createOrUpdateMenu(
                    "Settings",
                    "/vendor/settings",
                    "settings",
                    "Settings",
                    NavigationType.LINK,
                    PortalType.VENDOR,
                    null,
                    8,
                    List.of("Vendor Administrator"));
                    
                    */
        };
    }

    private NavigationMenu createOrUpdateMenu(
            String name,
            String href,
            String icon,
            String label,
            NavigationType type,
            PortalType portal,
            NavigationMenu parent,
            Integer order,
            List<String> permissions) {
    	

        Long parentId = parent == null ? null : parent.getId();

        NavigationMenu menu = navigationMenuRepository
                .findByNameAndParentId(name, parentId)
                .orElse(new NavigationMenu());

        menu.setName(name);
        menu.setHref(href);
        menu.setBasePath(href);
        menu.setIcon(icon);
        menu.setLabel(label);
        menu.setType(type);
        menu.setPortal(portal);
        menu.setParent(parent);
        menu.setOrder(order);
        menu.setPermissions(permissions);
        menu.setIsActive(true);
        menu.setHidden(false);

        return navigationMenuRepository.save(menu);
    }
}