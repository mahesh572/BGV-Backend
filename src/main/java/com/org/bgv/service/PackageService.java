package com.org.bgv.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.dto.PackageCheckCategoryResponse;
import com.org.bgv.dto.PackageRequestDTO;
import com.org.bgv.dto.PackageResponse;
import com.org.bgv.entity.BgvPackage;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.PackageCheckCategory;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.repository.PackageCheckCategoryRepository;
import com.org.bgv.repository.PackageRepository;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PackageService {

    private final PackageRepository packageRepository;
    private final PackageCheckCategoryRepository packageCheckCategoryRepository;
    private final CheckCategoryRepository checkCategoryRepository;

   
    @Transactional
    public PackageResponse createPackage(PackageRequestDTO request) {
        log.info("Creating new package with code: {}", request.getCode());
        
        // Check if package code already exists
        if (packageRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Package with code " + request.getCode() + " already exists");
        }

        // Create package entity
        BgvPackage packageEntity = BgvPackage.builder()
                .name(request.getName())
                .code(request.getCode())
                .description(request.getDescription())
                .customizable(request.getCustomizable())
                .basePrice(request.getBasePrice())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        // Save package
        BgvPackage savedPackage = packageRepository.save(packageEntity);
/*
        // Save package check categories if provided
        if (request.getPackageCheckCategories() != null) {
            List<PackageCheckCategory> packageCheckCategories = request.getPackageCheckCategories().stream()
                    .map(pccDTO -> {
                        CheckCategory category = checkCategoryRepository.findById(pccDTO.getCategoryId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "Check category not found with id: " + pccDTO.getCategoryId()));
                        
                        return PackageCheckCategory.builder()
                                .packageEntity(savedPackage)
                                .category(category)
                                .rulesData(pccDTO.getRulesData())
                                .build();
                    })
                    .collect(Collectors.toList());
            
            packageCheckCategoryRepository.saveAll(packageCheckCategories);
        }
*/
        return convertToResponse(savedPackage);
    }

    
    @Transactional(readOnly = true)
    public PackageResponse getPackageById(Long id) {
        log.info("Fetching package by id: {}", id);
        
        BgvPackage packageEntity = packageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Package not found with id: " + id));
        
        return convertToResponse(packageEntity);
    }

   
    @Transactional(readOnly = true)
    public PackageResponse getPackageByCode(String code) {
        log.info("Fetching package by code: {}", code);
        
        BgvPackage packageEntity = packageRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Package not found with code: " + code));
        
        return convertToResponse(packageEntity);
    }

    
    @Transactional(readOnly = true)
    public List<PackageResponse> getAllPackages() {
        log.info("Fetching all packages");
        
        return packageRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    
    @Transactional(readOnly = true)
    public List<PackageResponse> getActivePackages() {
        log.info("Fetching all active packages");
        
        return packageRepository.findByIsActiveTrue().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    
    @Transactional(readOnly = true)
    public List<PackageResponse> getCustomizablePackages() {
        log.info("Fetching all customizable packages");
        
        return packageRepository.findByCustomizableTrue().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    
    @Transactional
    public PackageResponse updatePackage(Long id, PackageRequestDTO request) {
        log.info("Updating package with id: {}", id);
        
        BgvPackage existingPackage = packageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Package not found with id: " + id));

        // Check if code is being changed and if it already exists
        if (!existingPackage.getCode().equals(request.getCode()) && 
            packageRepository.existsByCode(request.getCode())) {
            throw new IllegalArgumentException("Package with code " + request.getCode() + " already exists");
        }

        // Update package fields
        existingPackage.setName(request.getName());
        existingPackage.setCode(request.getCode());
        existingPackage.setDescription(request.getDescription());
        existingPackage.setCustomizable(request.getCustomizable());
        existingPackage.setBasePrice(request.getBasePrice());
        if (request.getIsActive() != null) {
            existingPackage.setIsActive(request.getIsActive());
        }

        // Update package check categories
        if (request.getPackageCheckCategories() != null) {
            // Remove existing categories
            packageCheckCategoryRepository.deleteByPackageId(id);
            /*
            // Add new categories
            List<PackageCheckCategory> packageCheckCategories = request.getPackageCheckCategories().stream()
                    .map(pccDTO -> {
                        CheckCategory category = checkCategoryRepository.findById(pccDTO.getCategoryId())
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "Check category not found with id: " + pccDTO.getCategoryId()));
                        
                        return PackageCheckCategory.builder()
                                .packageEntity(existingPackage)
                                .category(category)
                                .rulesData(pccDTO.getRulesData())
                                .build();
                    })
                    .collect(Collectors.toList());
            
            packageCheckCategoryRepository.saveAll(packageCheckCategories);
            */
        }

        BgvPackage updatedPackage = packageRepository.save(existingPackage);
        return convertToResponse(updatedPackage);
    }

   
    @Transactional
    public void deletePackage(Long id) {
        log.info("Deleting package with id: {}", id);
        
        BgvPackage packageEntity = packageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Package not found with id: " + id));
        
        packageRepository.delete(packageEntity);
    }

   
    @Transactional
    public PackageResponse activatePackage(Long id) {
        log.info("Activating package with id: {}", id);
        
        BgvPackage packageEntity = packageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Package not found with id: " + id));
        
        packageEntity.setIsActive(true);
        BgvPackage activatedPackage = packageRepository.save(packageEntity);
        return convertToResponse(activatedPackage);
    }

   
    @Transactional
    public PackageResponse deactivatePackage(Long id) {
        log.info("Deactivating package with id: {}", id);
        
        BgvPackage packageEntity = packageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Package not found with id: " + id));
        
        packageEntity.setIsActive(false);
        BgvPackage deactivatedPackage = packageRepository.save(packageEntity);
        return convertToResponse(deactivatedPackage);
    }

    private PackageResponse convertToResponse(BgvPackage packageEntity) {
        List<PackageCheckCategoryResponse> categoryResponses = packageEntity.getPackageCheckCategories().stream()
                .map(pcc -> PackageCheckCategoryResponse.builder()
                        .id(pcc.getId())
                        .categoryId(pcc.getCategory().getCategoryId()) // Use getCategoryId() instead of getId()
                        .categoryName(pcc.getCategory().getName())
                        .rulesData(pcc.getRulesData())
                        .build())
                .collect(Collectors.toList());

        return PackageResponse.builder()
                .pId(packageEntity.getPackageId())
                .name(packageEntity.getName())
                .code(packageEntity.getCode())
                .description(packageEntity.getDescription())
                .customizable(packageEntity.getCustomizable())
                .basePrice(packageEntity.getBasePrice())
                .isActive(packageEntity.getIsActive())
                .packageCheckCategories(categoryResponses)
                .build();
    }
}