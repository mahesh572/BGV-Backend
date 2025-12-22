package com.org.bgv.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.common.CheckCategoryRequest;
import com.org.bgv.common.CheckCategoryResponse;
import com.org.bgv.service.CheckCategoryService;


import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/check-categories")
@RequiredArgsConstructor
public class CheckCategoryController {
	
	private final CheckCategoryService checkCategoryService;
	
	@GetMapping
    public ResponseEntity<CustomApiResponse<List<CheckCategoryResponse>>> getAllCheckCategories() {
        List<CheckCategoryResponse> categories = checkCategoryService.getAllCheckCategories();
        return ResponseEntity.ok(CustomApiResponse.success(null, categories, HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse<CheckCategoryResponse>> getCheckCategoryById(@PathVariable Long id) {
        Optional<CheckCategoryResponse> category = checkCategoryService.getCheckCategoryById(id);
        return category.map(cat -> ResponseEntity.ok(CustomApiResponse.success(null, cat, HttpStatus.OK)))
                      .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                              .body(CustomApiResponse.failure("CheckCategory not found with id: " + id, HttpStatus.NOT_FOUND)));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<CustomApiResponse<CheckCategoryResponse>> getCheckCategoryByName(@PathVariable String name) {
        Optional<CheckCategoryResponse> category = checkCategoryService.getCheckCategoryByName(name);
        return category.map(cat -> ResponseEntity.ok(CustomApiResponse.success(null, cat, HttpStatus.OK)))
                      .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                              .body(CustomApiResponse.failure("CheckCategory not found with name: " + name, HttpStatus.NOT_FOUND)));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<CustomApiResponse<CheckCategoryResponse>> getCheckCategoryByCode(
            @PathVariable String code) {

        CheckCategoryResponse category =
                checkCategoryService.getCheckCategoryByCode(code);

        if (category == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(
                            "CheckCategory not found with code: " + code,
                            HttpStatus.NOT_FOUND
                    ));
        }

        return ResponseEntity.ok(
                CustomApiResponse.success(null, category, HttpStatus.OK)
        );
    }

    @PostMapping
    public ResponseEntity<CustomApiResponse<CheckCategoryResponse>> createCheckCategory(@RequestBody CheckCategoryRequest checkCategoryRequest) {
        try {
            CheckCategoryResponse createdCategory = checkCategoryService.createCheckCategory(checkCategoryRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CustomApiResponse.success("CheckCategory created successfully", createdCategory, HttpStatus.CREATED));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomApiResponse<CheckCategoryResponse>> updateCheckCategory(@PathVariable Long id, @RequestBody CheckCategoryRequest checkCategoryRequest) {
        try {
            CheckCategoryResponse updatedCategory = checkCategoryService.updateCheckCategory(id, checkCategoryRequest);
            return ResponseEntity.ok(CustomApiResponse.success("CheckCategory updated successfully", updatedCategory, HttpStatus.OK));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse<Void>> deleteCheckCategory(@PathVariable Long id) {
        try {
            checkCategoryService.deleteCheckCategory(id);
            return ResponseEntity.ok(CustomApiResponse.success("CheckCategory deleted successfully", null, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        }
    }

    @GetMapping("/exists/name/{name}")
    public ResponseEntity<CustomApiResponse<Boolean>> checkNameExists(@PathVariable String name) {
        boolean exists = checkCategoryService.existsByName(name);
        return ResponseEntity.ok(CustomApiResponse.success(null, exists, HttpStatus.OK));
    }

    @GetMapping("/exists/code/{code}")
    public ResponseEntity<CustomApiResponse<Boolean>> checkCodeExists(@PathVariable String code) {
        boolean exists = checkCategoryService.existsByCode(code);
        return ResponseEntity.ok(CustomApiResponse.success(null, exists, HttpStatus.OK));
    }
}
