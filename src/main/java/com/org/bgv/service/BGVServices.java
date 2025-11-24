package com.org.bgv.service;

import com.org.bgv.common.CheckCategoryResponse;
import com.org.bgv.entity.BGVCategory;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.CheckType;
import com.org.bgv.mapper.CheckCategoryMapper;
import com.org.bgv.repository.BGVCategoryRepository;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.repository.CheckTypeRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class BGVServices {

    @Autowired
    private BGVCategoryRepository bgvCategoryRepository;

    @Autowired
    private CheckTypeRepository checkTypeRepository;
    
    private final CheckCategoryRepository checkCategoryRepository;
    private final CheckCategoryMapper checkCategoryMapper;
    

    /**
     * Get all categories from database and convert to JSON structure manually
     */
    public List<Map<String, Object>> getAllCategoriesAndCheckVerification() {
        List<BGVCategory> categories = bgvCategoryRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();

        for (BGVCategory category : categories) {
            Map<String, Object> categoryMap = new HashMap<>();
            categoryMap.put("categoryId", category.getCategoryId());
            categoryMap.put("name", category.getName());
            categoryMap.put("label", category.getLabel());
            categoryMap.put("description", category.getDescription());
            categoryMap.put("isActive", category.getIsActive());
            categoryMap.put("createdAt", category.getCreatedAt());
            categoryMap.put("updatedAt", category.getUpdatedAt());

            // Get check types for this category
            List<CheckType> checkTypes = category.getCheckTypes();
            List<Map<String, Object>> checkTypesList = new ArrayList<>();

            for (CheckType checkType : checkTypes) {
                Map<String, Object> checkTypeMap = new HashMap<>();
                checkTypeMap.put("id", checkType.getCheckTypeId());
                checkTypeMap.put("name", checkType.getName());
                checkTypeMap.put("label", checkType.getLabel());
                checkTypesList.add(checkTypeMap);
            }

            categoryMap.put("checkTypes", checkTypesList);
            result.add(categoryMap);
        }

        return result;
    }

    /**
     * Get active categories only
     */
    public List<Map<String, Object>> getActiveCategories() {
        List<BGVCategory> categories = bgvCategoryRepository.findByIsActiveTrue();
        List<Map<String, Object>> result = new ArrayList<>();

        for (BGVCategory category : categories) {
            Map<String, Object> categoryMap = new HashMap<>();
            categoryMap.put("categoryId", category.getCategoryId());
            categoryMap.put("name", category.getName());
            categoryMap.put("label", category.getLabel());
            categoryMap.put("description", category.getDescription());
            categoryMap.put("isActive", category.getIsActive());
            categoryMap.put("createdAt", category.getCreatedAt());
            categoryMap.put("updatedAt", category.getUpdatedAt());

            // Get only active check types
            List<CheckType> checkTypes = category.getCheckTypes();
            List<Map<String, Object>> checkTypesList = new ArrayList<>();

            for (CheckType checkType : checkTypes) {
                if (Boolean.TRUE.equals(checkType.getIsActive())) {
                    Map<String, Object> checkTypeMap = new HashMap<>();
                    checkTypeMap.put("id", checkType.getCheckTypeId());
                    checkTypeMap.put("name", checkType.getName());
                    checkTypeMap.put("label", checkType.getLabel());
                    checkTypesList.add(checkTypeMap);
                }
            }

            categoryMap.put("checkTypes", checkTypesList);
            result.add(categoryMap);
        }

        return result;
    }

    /**
     * Add a new category manually
     */
    public BGVCategory addNewCategory(String name, String label, String description) {
        BGVCategory category = new BGVCategory();
        category.setName(name);
        category.setLabel(label);
        category.setDescription(description);
        category.setIsActive(true);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        return bgvCategoryRepository.save(category);
    }

    /**
     * Add a new check type to a category manually
     */
    public CheckType addNewCheckType(Long categoryId, String name, String label, String description) {
        BGVCategory category = bgvCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        CheckType checkType = new CheckType();
        checkType.setName(name);
        checkType.setLabel(label);
        checkType.setDescription(description);
        checkType.setCategory(category);
        checkType.setIsActive(true);
        checkType.setCreatedAt(LocalDateTime.now());
        checkType.setUpdatedAt(LocalDateTime.now());

        return checkTypeRepository.save(checkType);
    }
    
    
    // Check categories
    
   
    @Transactional(readOnly = true)
    public List<CheckCategoryResponse> getAllCheck_Categories() {
        log.info("Fetching all check categories");
        List<CheckCategory> categories = checkCategoryRepository.findAll();
        return categories.stream()
                .map(checkCategoryMapper::toResponse)
                .collect(Collectors.toList());
    }

   
    @Transactional(readOnly = true)
    public CheckCategoryResponse getCategoryWithRuleTypes(Long categoryId) throws Exception {
        log.info("Fetching category with rule types by ID: {}", categoryId);
        CheckCategory category = checkCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new Exception("Category not found with id: " + categoryId));
        return checkCategoryMapper.toDetailedResponse(category);
    }
    
    @Transactional(readOnly = true)
    public List<CheckCategoryResponse> getAllCategoriesWithRuleTypes() {
        log.info("Fetching all categories with their rule types");
        
        // Method 1: Using JOIN FETCH (if you have the relationship)
        List<CheckCategory> categories = checkCategoryRepository.findAll();
        
        // Method 2: If no relationship in entity, use separate calls
        // List<CheckCategory> categories = checkCategoryRepository.findAll();
        
        return checkCategoryMapper.toDetailedResponseList(categories);
    }
    
}