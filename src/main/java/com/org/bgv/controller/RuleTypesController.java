package com.org.bgv.controller;

import java.util.List;

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
import com.org.bgv.common.RuleTypesDTO;
import com.org.bgv.common.RuleTypesRequest;
import com.org.bgv.service.RuleTypesService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
public class RuleTypesController {

    private final RuleTypesService ruleTypesService;

    @PostMapping
    public ResponseEntity<CustomApiResponse<RuleTypesDTO>> createRule(@Valid @RequestBody RuleTypesRequest request) {
        try {
            RuleTypesDTO createdRule = ruleTypesService.createRule(request);
            return ResponseEntity.ok(CustomApiResponse.success("Rule created successfully", createdRule, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to create rule: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse<RuleTypesDTO>> getRuleById(@PathVariable Long id) {
        try {
            RuleTypesDTO rule = ruleTypesService.getRuleById(id);
            return ResponseEntity.ok(CustomApiResponse.success("Rule retrieved successfully", rule, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve rule: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping
    public ResponseEntity<CustomApiResponse<List<RuleTypesDTO>>> getAllRules() {
        try {
            List<RuleTypesDTO> rules = ruleTypesService.getAllRules();
            return ResponseEntity.ok(CustomApiResponse.success("Rules retrieved successfully", rules, HttpStatus.OK));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve rules: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<CustomApiResponse<List<RuleTypesDTO>>> getRulesByCategory(@PathVariable Long categoryId) {
        try {
            List<RuleTypesDTO> rules = ruleTypesService.getRulesByCategory(categoryId);
            return ResponseEntity.ok(CustomApiResponse.success("Rules retrieved successfully for category", rules, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve rules by category: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<CustomApiResponse<RuleTypesDTO>> getRuleByCode(@PathVariable String code) {
        try {
            RuleTypesDTO rule = ruleTypesService.getRuleByCode(code);
            return ResponseEntity.ok(CustomApiResponse.success("Rule retrieved successfully", rule, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve rule by code: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomApiResponse<RuleTypesDTO>> updateRule(
            @PathVariable Long id, 
            @Valid @RequestBody RuleTypesRequest request) {
        try {
            RuleTypesDTO updatedRule = ruleTypesService.updateRule(id, request);
            return ResponseEntity.ok(CustomApiResponse.success("Rule updated successfully", updatedRule, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to update rule: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CustomApiResponse<Void>> deleteRule(@PathVariable Long id) {
        try {
            ruleTypesService.deleteRule(id);
            return ResponseEntity.ok(CustomApiResponse.success("Rule deleted successfully", null, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to delete rule: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
