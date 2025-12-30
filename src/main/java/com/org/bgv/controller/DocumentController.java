package com.org.bgv.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.dto.DeleteResponse;
import com.org.bgv.dto.document.DocumentCategoryDto;
import com.org.bgv.service.DocumentService;
import com.org.bgv.service.VerificationCaseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

	 private final DocumentService documentService;
	
	// Get documents for specific section
    @GetMapping("/candidate/{candidateId}/section")
    public ResponseEntity<CustomApiResponse<DocumentCategoryDto>> getDocumentsBySection(
            @PathVariable Long candidateId,
            @RequestParam String section) {
    	log.info("DocumentController:::::getDocumentsBySection:::::candidateId:::::section::::{}{}",candidateId,section);
        try {
        	DocumentCategoryDto categories = documentService.getDocumentsBySection(candidateId, section);
            return ResponseEntity.ok(
                    CustomApiResponse.success("Documents retrieved successfully for section: " + section, categories, HttpStatus.OK)
            );
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure("Section not found: " + section, HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to retrieve documents: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
 // Upload multiple documents endpoint
    @PostMapping(value = "/candidate/{candidateId}/upload-multiple", consumes = "multipart/form-data")
    public ResponseEntity<CustomApiResponse<DocumentCategoryDto>> uploadMultipleDocuments(
            @PathVariable Long candidateId,
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("documentTypeId") Long documentTypeId,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "objectId", required = false) Long objectId,
            @RequestParam(value = "caseId",required = false) Long caseId
            )
          {
    	
    	log.info("Received upload request for profile::::DocumentTypeId::::CategoryId::caseId::::: {}{}{}{}", candidateId,documentTypeId,categoryId,caseId);
    	
        try {
        	DocumentCategoryDto uploadedDocuments = documentService.createDocuments(
                files, candidateId,categoryId,documentTypeId,objectId,caseId);
        	log.info("***************************uploaded successfully");
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CustomApiResponse.success("Documents uploaded successfully", uploadedDocuments, HttpStatus.CREATED));
                    
        } catch (RuntimeException e) {
        	log.error("Upload error:", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.BAD_REQUEST));
        } catch (Exception e) {
        	log.error("Upload error:", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to upload documents: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    
    @DeleteMapping("/candidate/{candidateId}/documents/{docId}")
    public ResponseEntity<CustomApiResponse<DeleteResponse>> deleteDocument(
    		
    		@PathVariable Long docId
            ) {
    	// logger.info("Received upload request for profile: {}", profileId);
    	
    	// logger.info("DocumentTypeId: {}, CategoryId: {}", docTypeId, category);
        try {
            DeleteResponse deleteResponse = documentService.deleteDocument(docId);
            return ResponseEntity.ok(CustomApiResponse.success("Document deleted successfully", deleteResponse, HttpStatus.OK));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(CustomApiResponse.failure(e.getMessage(), HttpStatus.NOT_FOUND));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure("Failed to delete document: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
}
