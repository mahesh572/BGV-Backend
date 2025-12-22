package com.org.bgv.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.service.VendorDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/vendor/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VendorDashboardController {
	
private final VendorDashboardService vendorDashboardService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDashboardData(@RequestParam Long vendorId) {
        Map<String, Object> dashboardData = vendorDashboardService.getVendorDashboardData(vendorId);
        return ResponseEntity.ok(dashboardData);
    }
}
