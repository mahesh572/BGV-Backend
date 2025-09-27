package com.org.bgv.service;

import com.org.bgv.dto.DeleteResponse;
import com.org.bgv.dto.DocumentCategoryGroup;
import com.org.bgv.dto.DocumentResponse;
import com.org.bgv.dto.DocumentTypeResponse;
import com.org.bgv.entity.BaseDocument;
import com.org.bgv.entity.Document;
import com.org.bgv.entity.DocumentCategory;
import com.org.bgv.entity.DocumentType;
import com.org.bgv.entity.EducationDocuments;
import com.org.bgv.entity.IdentityDocuments;
import com.org.bgv.entity.IdentityProof;
import com.org.bgv.entity.ProfessionalDocuments;
import com.org.bgv.entity.Profile;
import com.org.bgv.repository.DocumentCategoryRepository;
import com.org.bgv.repository.DocumentRepository;
import com.org.bgv.repository.DocumentTypeRepository;
import com.org.bgv.repository.EducationDocumentsRepository;
import com.org.bgv.repository.IdentityDocumentsRepository;
import com.org.bgv.repository.IdentityProofRepository;
import com.org.bgv.repository.ProfessionalDocumentsRepository;
import com.org.bgv.repository.ProfileRepository;
import com.org.bgv.s3.S3StorageService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.hibernate.service.spi.ServiceException;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ProfessionalDocumentsRepository professionalDocumentsRepository;
    private final EducationDocumentsRepository educationDocumentsRepository;
    private final IdentityDocumentsRepository identityDocumentsRepository;
    private final S3StorageService s3StorageService;
    private final ProfileRepository profileRepository;
    private final DocumentCategoryRepository categoryRepository;
    private final DocumentTypeRepository typeRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentCategoryRepository documentCategoryRepository;
    private final IdentityProofRepository identityProofRepository;

	public List<DocumentResponse> createDocuments(List<MultipartFile> files, Long profileId, Long categoryId,
			Long typeId,Long objectId) {
		// Validate inputs
		if (files == null || files.isEmpty()) {
			throw new RuntimeException("No files provided for upload");
		}

		if (files.size() > 10) { // Limit to prevent abuse
			throw new RuntimeException("Maximum 10 files allowed per request");
		}
		DocumentType type = null;
		Profile profile = profileRepository.findById(profileId)
				.orElseThrow(() -> new RuntimeException("Profile not found: " + profileId));
		DocumentCategory category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));
		
		if(typeId!=null && typeId>0) {
		 type = typeRepository.findById(typeId)
				.orElseThrow(() -> new RuntimeException("DocumentType not found: " + typeId));
		}
		List<DocumentResponse> successfulUploads = new ArrayList<>();
		List<String> failedUploads = new ArrayList<>();

		for (MultipartFile file : files) {
			try {
				// Validate individual file
				if (file.isEmpty()) {
					failedUploads.add(file.getOriginalFilename() + " (empty file)");
					continue;
				}

				if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
					failedUploads.add(file.getOriginalFilename() + " (file too large)");
					continue;
				}

				Pair<String, String> uploadResult = s3StorageService.uploadFile(file, category.getName());
				
				if(category.getName().contains("IDENTITY")) {
					
					IdentityProof identityProof = IdentityProof.builder().profile(profile).build();
					identityProof = identityProofRepository.save(identityProof);
					
					IdentityDocuments document = IdentityDocuments.builder().profile(profile).category(category).type_id(type)
							.file_url(uploadResult.getFirst()).file_size(file.getSize()).awsDocKey(uploadResult.getSecond())
							.status("UPLOADED").uploadedAt(LocalDateTime.now())
							.objectId(identityProof.getId())
							.build();
					IdentityDocuments saved = identityDocumentsRepository.save(document);
					successfulUploads.add(convertToDocumentResponse(document));
					
				}else if (category.getName().contains("EDUCATION")) {
					EducationDocuments document = EducationDocuments.builder().profile(profile).category(category).type_id(type)
							.file_url(uploadResult.getFirst()).file_size(file.getSize()).awsDocKey(uploadResult.getSecond())
							.status("UPLOADED").uploadedAt(LocalDateTime.now())
							.objectId(objectId)
							.build();
					EducationDocuments saved = educationDocumentsRepository.save(document);
					successfulUploads.add(convertToDocumentResponse(document));
					
				}else if (category.getName().contains("PROFESSIONAL")) {
					ProfessionalDocuments document = ProfessionalDocuments.builder().profile(profile).category(category).type_id(type)
							.file_url(uploadResult.getFirst()).file_size(file.getSize()).awsDocKey(uploadResult.getSecond())
							.status("UPLOADED").uploadedAt(LocalDateTime.now())
							.objectId(objectId)
							.build();
					ProfessionalDocuments saved = professionalDocumentsRepository.save(document);
					successfulUploads.add(convertToDocumentResponse(document));
				}else {
					Document document = Document.builder().profile(profile).category(category).type_id(type)
							.file_url(uploadResult.getFirst()).file_size(file.getSize()).awsDocKey(uploadResult.getSecond())
							.status("UPLOADED").uploadedAt(LocalDateTime.now())
							.object_id(objectId)
							.build();
					Document saved = documentRepository.save(document);
					successfulUploads.add(convertToDocumentResponse(document));
				}

				

				

			} catch (Exception e) {
				failedUploads.add(file.getOriginalFilename() + " (" + e.getMessage() + ")");
			}
		}

		if (!failedUploads.isEmpty()) {
			String errorMessage = "Some files failed to upload: " + String.join(", ", failedUploads);
			if (successfulUploads.isEmpty()) {
				throw new RuntimeException("All files failed to upload: " + String.join(", ", failedUploads));
			}
// You could log this or include it in the response
			System.err.println(errorMessage);
		}

		return successfulUploads;
	}
	
	@Transactional
    public DeleteResponse deleteDocument(Long docId, Long docTypeId,String category) {
    	
    	
    	if(category!=null) {
    		if(category.contains("identity")) {
    			IdentityDocuments identityDocuments = identityDocumentsRepository.findById(docId).get();
    			deleteDocfromS3(identityDocuments.getAwsDocKey());
    			identityDocumentsRepository.delete(identityDocuments);
    		}else if(category.contains("education")) {
    			EducationDocuments educationDocuments = educationDocumentsRepository.findById(docId).get();
    			deleteDocfromS3(educationDocuments.getAwsDocKey());
    			educationDocumentsRepository.delete(educationDocuments);
    		}else if(category.contains("profssional")) {
    			ProfessionalDocuments professionalDocuments = professionalDocumentsRepository.findById(docId).get();
    			deleteDocfromS3(professionalDocuments.getAwsDocKey());
    			professionalDocumentsRepository.delete(professionalDocuments);
    		}else {
    			Document document = documentRepository.findById(docId)
    	                .orElseThrow(() -> new RuntimeException("Document not found: " + docId));
    			 deleteDocfromS3(document.getAwsDocKey());
    			 documentRepository.delete(document);
    		}
    	}
        

        
        
        return new DeleteResponse(docId, "Document deleted successfully");
    }
    
	@Transactional
	public void deleteOtherDocuments(Long profileId) {
		List<Document> documentList = documentRepository.findByProfile_ProfileId(profileId);
		documentRepository.deleteAll(documentList);
		
	}
	
	
	
    public Map<String, Object> getDocumentUploadConfig() {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> sections = new ArrayList();
        
        // Get all categories
        List<DocumentCategory> categories = categoryRepository.findAll();
        
        for (DocumentCategory category : categories) {
            Map<String, Object> section = new HashMap<>();
            section.put("sectionId", category.getName().toLowerCase());
            section.put("sectionName", category.getLabel());
            section.put("sectionDescription", category.getDescription());
            section.put("categoryId", category.getCategoryId());
            section.put("categoryName", category.getName());
            section.put("categoryLabel", category.getLabel());
            
            // Get document types for this category
            List<DocumentType> documentTypes = typeRepository.findByCategory(category);
            List<Map<String, Object>> dropdownOptions = new ArrayList<>();
            
            for (DocumentType docType : documentTypes) {
                Map<String, Object> option = new HashMap<>();
                option.put("value", docType.getName().toLowerCase());
                option.put("label", docType.getName());
                option.put("documentTypeId", docType.getDoc_type_id());
                option.put("required", true); // You can add this field to DocumentType entity
                option.put("maxFiles", 3); // Configurable
                option.put("allowedFormats", Arrays.asList("PDF", "JPG", "PNG"));
                option.put("maxSizeMB", 10);
                dropdownOptions.add(option);
            }
            
            section.put("dropdownOptions", dropdownOptions);
            sections.add(section);
        }
        
        response.put("documentSections", sections);
        
        // Add upload configuration
        Map<String, Object> uploadConfig = new HashMap();
        uploadConfig.put("maxTotalSizeMB", 100);
        uploadConfig.put("maxFilesPerDocument", 5);
        uploadConfig.put("allowedFormats", Arrays.asList("PDF", "JPG", "JPEG", "PNG", "DOC", "DOCX"));
        uploadConfig.put("autoUpload", false);
        uploadConfig.put("multipleUploads", true);
        
        response.put("uploadConfig", uploadConfig);
        
        return response;
    }
    public List<DocumentCategoryGroup> getDocumentsByProfileGroupedByCategory(Long profileId) {
        try {
            // Verify profile exists with better error message
            Profile profile = profileRepository.findById(profileId)
                    .orElseThrow(() -> new EntityNotFoundException("Profile not found with ID: " + profileId));

            // Use parallel processing for better performance with large datasets
            List<CompletableFuture<List<? extends BaseDocument>>> futures = Arrays.asList(
                CompletableFuture.supplyAsync(() -> documentRepository.findByProfile_ProfileId(profileId))
                
            );

            // Wait for all async operations to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            // Combine all documents from different repositories
            List<BaseDocument> allDocuments = futures.stream()
                    .map(CompletableFuture::join)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            if (allDocuments.isEmpty()) {
                return Collections.emptyList();
            }

            // Pre-fetch categories to avoid N+1 query problem
            Set<Long> categoryIds = allDocuments.stream()
                    .map(doc -> doc.getCategory().getCategoryId())
                    .collect(Collectors.toSet());
            
            Map<Long, DocumentCategory> categoriesMap = categoryRepository.findAllById(categoryIds)
                    .stream()
                    .collect(Collectors.toMap(DocumentCategory::getCategoryId, Function.identity()));

            // Group documents by category with proper error handling
            Map<DocumentCategory, List<BaseDocument>> groupedByCategory = allDocuments.stream()
                    .collect(Collectors.groupingBy(
                        doc -> {
                            DocumentCategory category = doc.getCategory();
                            if (category == null) {
                               
                                return getUnknownCategory();
                            }
                            return categoriesMap.getOrDefault(category.getCategoryId(), getUnknownCategory());
                        },
                        LinkedHashMap::new, // Maintain insertion order
                        Collectors.toList()
                    ));

            // Convert to response DTOs
            return groupedByCategory.entrySet().stream()
                    .map(entry -> createCategoryGroup(entry.getKey(), entry.getValue()))
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(DocumentCategoryGroup::getCategoryId))
                    .collect(Collectors.toList());

        } catch (EntityNotFoundException e) {
            throw e; // Re-throw specific exceptions
        } catch (Exception e) {
           
            throw new ServiceException("Failed to retrieve documents for profile: " + profileId, e);
        }
    }

    private DocumentCategoryGroup createCategoryGroup(DocumentCategory category, List<BaseDocument> documents) {
        if (category == null || documents == null || documents.isEmpty()) {
            return null;
        }

        List<DocumentResponse> documentResponses = documents.stream()
                .map(this::convertToDocumentResponse)
                .filter(Objects::nonNull)
               // .sorted(Comparator.comparing(DocumentResponse::getUploadedAt).reversed())
                .collect(Collectors.toList());

        if (documentResponses.isEmpty()) {
            return null;
        }

        return DocumentCategoryGroup.builder()
                .categoryId(category.getCategoryId())
                .categoryName(category.getName())
                .description(category.getDescription())
                .documents(documentResponses)
               // .totalDocuments(documentResponses.size())
              //  .verifiedCount(countVerifiedDocuments(documentResponses))
                .build();
    }

    private DocumentResponse convertToDocumentResponse(BaseDocument document) {
        try {
            return DocumentResponse.builder()
                    .doc_id(document.getDoc_id())
                    .category_id(document.getCategory() != null ? document.getCategory().getCategoryId() : null)
                    .doc_type_id(document.getType_id() != null ? document.getType_id().getDoc_type_id() : null)
                    .file_url(document.getFile_url())
                    .file_size(document.getFile_size())
                    .status(document.getStatus())
                    .uploadedAt(document.getUploadedAt())
                    .verifiedAt(document.getVerifiedAt())
                    .comments(document.getComments())
                    .awsDocKey(document.getAwsDocKey())
                    
                    .file_name(extractFileName(document.getFile_url()))
                    .file_type(extractFileType(document.getFile_url()))
                    .build();
        } catch (Exception e) {
            
            return null;
        }
    }

    private String extractFileName(String fileUrl) {
        if (fileUrl == null) return null;
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }

    private String extractFileType(String fileUrl) {
        if (fileUrl == null) return null;
        int dotIndex = fileUrl.lastIndexOf(".");
        return dotIndex > 0 ? fileUrl.substring(dotIndex + 1).toUpperCase() : "UNKNOWN";
    }

    private long countVerifiedDocuments(List<DocumentResponse> documents) {
        return documents.stream()
                .filter(doc -> "VERIFIED".equals(doc.getStatus()))
                .count();
    }

    private DocumentCategory getUnknownCategory() {
        return DocumentCategory.builder()
                .categoryId(-1L)
                .name("UNCATEGORIZED")
                .description("Documents without category")
                .build();
    }
 // Get document types by category name
    public List<DocumentTypeResponse> getDocumentTypesByCategoryId(Long categoryId) {
        List<DocumentType> documentTypes = documentTypeRepository.findByCategoryCategoryId(categoryId);
        
        return documentTypes.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    public List<DocumentTypeResponse> getDocumentTypesByCategoryNameIgnoreCase(String categoryName) {
        // Step 1: Find category by name (case-insensitive)
        Optional<DocumentCategory> categoryOpt = documentCategoryRepository.findByNameContainingIgnoreCase(categoryName);
        
        if (categoryOpt.isEmpty()) {
            throw new RuntimeException("Category not found with name: " + categoryName);
        }
        
        DocumentCategory category = categoryOpt.get();
        
        // Step 2: Find document types by category ID
        List<DocumentType> documentTypes = documentTypeRepository.findByCategoryCategoryId(category.getCategoryId());
        
        if (documentTypes.isEmpty()) {
            throw new RuntimeException("No document types found for category: " + categoryName);
        }
        
        return documentTypes.stream()
                .map(documentType -> convertToResponse(documentType, category))
                .collect(Collectors.toList());
    }
 
    private DocumentTypeResponse convertToResponse(DocumentType documentType) {
        return DocumentTypeResponse.builder()
                .categoryId(documentType.getCategory() != null ? documentType.getCategory().getCategoryId() : null)
                .doc_type_id(documentType.getDoc_type_id())
                .name(documentType.getName())
                .categoryName(documentType.getCategory() != null ? documentType.getCategory().getName() : null)
                .build();
    }
    
    private DocumentTypeResponse convertToResponse(DocumentType documentType, DocumentCategory category) {
        return DocumentTypeResponse.builder()
                .categoryId(category.getCategoryId())
                .doc_type_id(documentType.getDoc_type_id())
                .name(documentType.getName())
                .categoryName(category.getName())
              //  .categoryDescription(category.getDescription())
              //  .categoryLabel(category.getLabel())
                .build();
    }
    private void deleteDocfromS3(String key) {
    	 if (key != null) {
             s3StorageService.deleteFile(key);
         }
    }
}
