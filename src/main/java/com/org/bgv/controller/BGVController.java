package com.org.bgv.controller;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.common.CheckCategoryResponse;
import com.org.bgv.service.BGVServices;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bgv")
@Slf4j
public class BGVController {

    @Autowired
    private BGVServices bgvServices;

    /*
    @GetMapping("/categories/json")
    public ResponseEntity<CustomApiResponse<List<Map<String, Object>>>> getBGVJsonStructure() {
        try {
            List<Map<String, Object>> categories = bgvServices.generateBGVJsonStructure();
            return ResponseEntity.ok(CustomApiResponse.success("BGV categories retrieved successfully", categories, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve BGV categories: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
*/
    @GetMapping("/verification/categories")
    public ResponseEntity<CustomApiResponse<List<Map<String, Object>>>> getAllCategories() {
        try {
            List<Map<String, Object>> categories = bgvServices.getAllCategoriesAndCheckVerification();
            return ResponseEntity.ok(CustomApiResponse.success("Categories retrieved successfully", categories, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve categories: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @GetMapping("/verification/categories/active")
    public ResponseEntity<CustomApiResponse<List<Map<String, Object>>>> getActiveCategories() {
        try {
            List<Map<String, Object>> categories = bgvServices.getActiveCategories();
            return ResponseEntity.ok(CustomApiResponse.success("Active categories retrieved successfully", categories, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve active categories: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    

    @PostMapping("/verification/categories")
    public ResponseEntity<CustomApiResponse<Object>> addCategory(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String label = request.get("label");
            String description = request.get("description");
            
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(CustomApiResponse.failure("Category name is required", HttpStatus.BAD_REQUEST));
            }
            
            Object category = bgvServices.addNewCategory(name, label, description);
            return ResponseEntity.ok(CustomApiResponse.success("Category created successfully", category, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to create category: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/check-types")
    public ResponseEntity<CustomApiResponse<Object>> addCheckType(@RequestBody Map<String, Object> request) {
        try {
            Long categoryId = Long.valueOf(request.get("categoryId").toString());
            String name = (String) request.get("name");
            String label = (String) request.get("label");
            String description = (String) request.get("description");
            
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(CustomApiResponse.failure("Check type name is required", HttpStatus.BAD_REQUEST));
            }
            
            Object checkType = bgvServices.addNewCheckType(categoryId, name, label, description);
            return ResponseEntity.ok(CustomApiResponse.success("Check type created successfully", checkType, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to create check type: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    
    

    @GetMapping("/check/categories")
    public ResponseEntity<CustomApiResponse<List<CheckCategoryResponse>>> getAllCheckCategories() {
        List<CheckCategoryResponse> categories = bgvServices.getAllCheck_Categories();
        return ResponseEntity.ok(CustomApiResponse.success(null, categories, HttpStatus.OK));
    }
    
    @GetMapping("/check/categories/with-ruletypes")
    public ResponseEntity<CustomApiResponse<List<CheckCategoryResponse>>> getCategoryWithRuleTypes(
           ) throws Exception {
    	List<CheckCategoryResponse> categoryResponse =  bgvServices.getAllCategoriesWithRuleTypes();
        return ResponseEntity.ok(CustomApiResponse.success(null, categoryResponse, HttpStatus.OK));
    }
    
    @GetMapping("/document-types/by-category")
    public ResponseEntity<CustomApiResponse<Map<Long, List<Map<String, Object>>>>> getDocumentTypesByCategory() {
        try {
            Map<Long, List<Map<String, Object>>> documentTypes = bgvServices.getDocumentTypesByCategory();
            log.info("getDocumentTypesByCategory:::::::{}",documentTypes);
            return ResponseEntity.ok(CustomApiResponse.success(
                "Document types retrieved successfully", 
                documentTypes, 
                HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CustomApiResponse.failure(
                    "Failed to retrieve document types: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR
                ));
        }
    }
    
    @GetMapping("/rule-types/by-category")
    public ResponseEntity<CustomApiResponse<Map<Long, List<Map<String, Object>>>>> getRuleTypesByCategoryMap() {
        try {
            Map<Long, List<Map<String, Object>>> documentTypes = bgvServices.getRuleTypesByCategoryMap();
            log.info("getRuleTypesByCategory:::::::{}",documentTypes);
            return ResponseEntity.ok(CustomApiResponse.success(
                "Rule types retrieved successfully", 
                documentTypes, 
                HttpStatus.OK
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CustomApiResponse.failure(
                    "Failed to retrieve rule types: " + e.getMessage(), 
                    HttpStatus.INTERNAL_SERVER_ERROR
                ));
        }
    }
    
    
/*
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<CustomApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        try {
            // You'll need to implement this method in BGVServices
            bgvServices.deleteCategory(id);
            return ResponseEntity.ok(CustomApiResponse.success("Category deleted successfully", null, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to delete category: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping("/check-types/{id}")
    public ResponseEntity<CustomApiResponse<Void>> deleteCheckType(@PathVariable Long id) {
        try {
            // You'll need to implement this method in BGVServices
            bgvServices.deleteCheckType(id);
            return ResponseEntity.ok(CustomApiResponse.success("Check type deleted successfully", null, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to delete check type: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<CustomApiResponse<Object>> updateCategory(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String label = request.get("label");
            String description = request.get("description");
            Boolean isActive = request.get("isActive") != null ? Boolean.valueOf(request.get("isActive")) : null;
            
            // You'll need to implement this method in BGVServices
            Object updatedCategory = bgvServices.updateCategory(id, name, label, description, isActive);
            return ResponseEntity.ok(CustomApiResponse.success("Category updated successfully", updatedCategory, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to update category: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/check-types/{id}")
    public ResponseEntity<CustomApiResponse<Object>> updateCheckType(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            String label = (String) request.get("label");
            String description = (String) request.get("description");
            Boolean isActive = request.get("isActive") != null ? Boolean.valueOf(request.get("isActive").toString()) : null;
            
            // You'll need to implement this method in BGVServices
            Object updatedCheckType = bgvServices.updateCheckType(id, name, label, description, isActive);
            return ResponseEntity.ok(CustomApiResponse.success("Check type updated successfully", updatedCheckType, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to update check type: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    */
}