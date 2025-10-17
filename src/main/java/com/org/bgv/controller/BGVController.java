package com.org.bgv.controller;

import com.org.bgv.api.response.ApiResponse;
import com.org.bgv.service.BGVServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bgv")
public class BGVController {

    @Autowired
    private BGVServices bgvServices;

    /*
    @GetMapping("/categories/json")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getBGVJsonStructure() {
        try {
            List<Map<String, Object>> categories = bgvServices.generateBGVJsonStructure();
            return ResponseEntity.ok(ApiResponse.success("BGV categories retrieved successfully", categories, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to retrieve BGV categories: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
*/
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllCategories() {
        try {
            List<Map<String, Object>> categories = bgvServices.getAllCategoriesAndCheck();
            return ResponseEntity.ok(ApiResponse.success("Categories retrieved successfully", categories, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to retrieve categories: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/categories/active")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getActiveCategories() {
        try {
            List<Map<String, Object>> categories = bgvServices.getActiveCategories();
            return ResponseEntity.ok(ApiResponse.success("Active categories retrieved successfully", categories, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to retrieve active categories: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    

    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<Object>> addCategory(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String label = request.get("label");
            String description = request.get("description");
            
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.failure("Category name is required", HttpStatus.BAD_REQUEST));
            }
            
            Object category = bgvServices.addNewCategory(name, label, description);
            return ResponseEntity.ok(ApiResponse.success("Category created successfully", category, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to create category: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PostMapping("/check-types")
    public ResponseEntity<ApiResponse<Object>> addCheckType(@RequestBody Map<String, Object> request) {
        try {
            Long categoryId = Long.valueOf(request.get("categoryId").toString());
            String name = (String) request.get("name");
            String label = (String) request.get("label");
            String description = (String) request.get("description");
            
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ApiResponse.failure("Check type name is required", HttpStatus.BAD_REQUEST));
            }
            
            Object checkType = bgvServices.addNewCheckType(categoryId, name, label, description);
            return ResponseEntity.ok(ApiResponse.success("Check type created successfully", checkType, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to create check type: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
/*
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        try {
            // You'll need to implement this method in BGVServices
            bgvServices.deleteCategory(id);
            return ResponseEntity.ok(ApiResponse.success("Category deleted successfully", null, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to delete category: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping("/check-types/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCheckType(@PathVariable Long id) {
        try {
            // You'll need to implement this method in BGVServices
            bgvServices.deleteCheckType(id);
            return ResponseEntity.ok(ApiResponse.success("Check type deleted successfully", null, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to delete check type: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<ApiResponse<Object>> updateCategory(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String label = request.get("label");
            String description = request.get("description");
            Boolean isActive = request.get("isActive") != null ? Boolean.valueOf(request.get("isActive")) : null;
            
            // You'll need to implement this method in BGVServices
            Object updatedCategory = bgvServices.updateCategory(id, name, label, description, isActive);
            return ResponseEntity.ok(ApiResponse.success("Category updated successfully", updatedCategory, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to update category: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/check-types/{id}")
    public ResponseEntity<ApiResponse<Object>> updateCheckType(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            String label = (String) request.get("label");
            String description = (String) request.get("description");
            Boolean isActive = request.get("isActive") != null ? Boolean.valueOf(request.get("isActive").toString()) : null;
            
            // You'll need to implement this method in BGVServices
            Object updatedCheckType = bgvServices.updateCheckType(id, name, label, description, isActive);
            return ResponseEntity.ok(ApiResponse.success("Check type updated successfully", updatedCheckType, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.failure("Failed to update check type: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    */
}