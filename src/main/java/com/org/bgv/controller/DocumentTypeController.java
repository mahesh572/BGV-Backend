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
import com.org.bgv.common.DocumentTypeRequest;
import com.org.bgv.common.DocumentTypeResponse;
import com.org.bgv.service.DocumentTypeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/document-types")
@RequiredArgsConstructor
public class DocumentTypeController {

    private final DocumentTypeService documentTypeService;

    @GetMapping
    public ResponseEntity<CustomApiResponse<List<DocumentTypeResponse>>> getAllDocumentTypes() {
        List<DocumentTypeResponse> documentTypes = documentTypeService.getAllDocumentTypes();
        return ResponseEntity.ok(CustomApiResponse.success(null, documentTypes, HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomApiResponse<DocumentTypeResponse>> getDocumentTypeById(@PathVariable Long id) {
        Optional<DocumentTypeResponse> documentType = documentTypeService.getDocumentTypeById(id);
        return documentType.map(dt -> ResponseEntity.ok(CustomApiResponse.success(null, dt, HttpStatus.OK)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(CustomApiResponse.failure("DocumentType not found with id: " + id, HttpStatus.NOT_FOUND)));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<CustomApiResponse<DocumentTypeResponse>> getDocumentTypeByName(@PathVariable String name) {
        Optional<DocumentTypeResponse> documentType = documentTypeService.getDocumentTypeByName(name);
        return documentType.map(dt -> ResponseEntity.ok(CustomApiResponse.success(null, dt, HttpStatus.OK)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(CustomApiResponse.failure("DocumentType not found with name: " + name, HttpStatus.NOT_FOUND)));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<CustomApiResponse<DocumentTypeResponse>> getDocumentTypeByCode(@PathVariable String code) {
        Optional<DocumentTypeResponse> documentType = documentTypeService.getDocumentTypeByCode(code);
        return documentType.map(dt -> ResponseEntity.ok(CustomApiResponse.success(null, dt, HttpStatus.OK)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(CustomApiResponse.failure("DocumentType not found with code: " + code, HttpStatus.NOT_FOUND)));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<CustomApiResponse<List<DocumentTypeResponse>>> getDocumentTypesByCategory(@PathVariable Long categoryId) {
        List<DocumentTypeResponse> documentTypes = documentTypeService.getDocumentTypesByCategory(categoryId);
        return ResponseEntity.ok(CustomApiResponse.success(null, documentTypes, HttpStatus.OK));
    }

    @GetMapping("/category/name/{categoryName}")
    public ResponseEntity<CustomApiResponse<List<DocumentTypeResponse>>> getDocumentTypesByCategoryName(@PathVariable String categoryName) {
        List<DocumentTypeResponse> documentTypes = documentTypeService.getDocumentTypesByCategoryName(categoryName);
        return ResponseEntity.ok(CustomApiResponse.success(null, documentTypes, HttpStatus.OK));
    }

    @GetMapping("/required/{isRequired}")
    public ResponseEntity<CustomApiResponse<List<DocumentTypeResponse>>> getRequiredDocumentTypes(@PathVariable boolean isRequired) {
        List<DocumentTypeResponse> documentTypes = documentTypeService.getRequiredDocumentTypes(isRequired);
        return ResponseEntity.ok(CustomApiResponse.success(null, documentTypes, HttpStatus.OK));
    }

    @PostMapping
    public ResponseEntity<CustomApiResponse<DocumentTypeResponse>> createDocumentType(@RequestBody DocumentTypeRequest documentTypeRequest) {
        try {
            DocumentTypeResponse createdDocumentType = documentTypeService.createDocumentType(documentTypeRequest);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CustomApiResponse.success("DocumentType created successfully", createdDocumentType, HttpStatus.CREATED));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomApiResponse<DocumentTypeResponse>> updateDocumentType(@PathVariable Long id, @RequestBody DocumentTypeRequest documentTypeRequest) {
        try {
            DocumentTypeResponse updatedDocumentType = documentTypeService.updateDocumentType(id, documentTypeRequest);
            return ResponseEntity.ok(CustomApiResponse.success("DocumentType updated successfully", updatedDocumentType, HttpStatus.OK));
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
    public ResponseEntity<CustomApiResponse<Void>> deleteDocumentType(@PathVariable Long id) {
        try {
            documentTypeService.deleteDocumentType(id);
            return ResponseEntity.ok(CustomApiResponse.success("DocumentType deleted successfully", null, HttpStatus.OK));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        }
    }

    @GetMapping("/exists/name/{name}")
    public ResponseEntity<CustomApiResponse<Boolean>> checkNameExists(@PathVariable String name) {
        boolean exists = documentTypeService.existsByName(name);
        return ResponseEntity.ok(CustomApiResponse.success(null, exists, HttpStatus.OK));
    }

    @GetMapping("/exists/code/{code}")
    public ResponseEntity<CustomApiResponse<Boolean>> checkCodeExists(@PathVariable String code) {
        boolean exists = documentTypeService.existsByCode(code);
        return ResponseEntity.ok(CustomApiResponse.success(null, exists, HttpStatus.OK));
    }
}