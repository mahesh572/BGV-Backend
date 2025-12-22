package com.org.bgv.service;

import com.org.bgv.config.SecurityUtils;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.controller.ProfileController;
import com.org.bgv.dto.CheckCategoryGroup;
import com.org.bgv.dto.DeleteResponse;
import com.org.bgv.dto.DocumentResponse;
import com.org.bgv.dto.DocumentTypeResponse;
import com.org.bgv.dto.FieldDTO;
import com.org.bgv.dto.document.CategoriesDTO;
import com.org.bgv.dto.document.CompanyDto;
import com.org.bgv.dto.document.DocumentCategoryDto;
import com.org.bgv.dto.document.DocumentTypeDto;
import com.org.bgv.dto.document.EducationDTO;
import com.org.bgv.dto.document.FileDTO;
import com.org.bgv.entity.BaseDocument;
import com.org.bgv.entity.Candidate;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.Document;
import com.org.bgv.entity.DocumentType;
//import com.org.bgv.entity.EducationDocuments;
import com.org.bgv.entity.EducationHistory;
import com.org.bgv.entity.IdentityDocuments;
import com.org.bgv.entity.IdentityProof;
import com.org.bgv.entity.Other;
import com.org.bgv.entity.ProfessionalDocuments;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.entity.VerificationCaseDocument;
import com.org.bgv.entity.WorkExperience;
import com.org.bgv.repository.CandidateRepository;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.repository.DocumentRepository;
import com.org.bgv.repository.DocumentTypeRepository;
import com.org.bgv.repository.EducationHistoryRepository;
import com.org.bgv.repository.IdentityDocumentsRepository;
import com.org.bgv.repository.IdentityProofRepository;
import com.org.bgv.repository.OtherRepository;
import com.org.bgv.repository.ProfessionalDocumentsRepository;
import com.org.bgv.repository.ProfileRepository;
import com.org.bgv.repository.WorkExperienceRepository;
import com.org.bgv.s3.S3StorageService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final IdentityDocumentsRepository identityDocumentsRepository;
    private final S3StorageService s3StorageService;
    private final ProfileRepository profileRepository;
    private final CheckCategoryRepository checkCategoryRepository;
    private final DocumentTypeRepository typeRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final IdentityProofRepository identityProofRepository;
    private final EducationHistoryRepository educationHistoryRepository;
    private final WorkExperienceRepository workExperienceRepository;
    private final OtherRepository otherRepository;
    private final CandidateRepository candidateRepository;
    private final VerificationCaseService verificationCaseService;
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

	public DocumentCategoryDto createDocuments(List<MultipartFile> files, Long profileId, Long categoryId,
			Long typeId,Long objectId,Long candidateId) {
		DocumentCategoryDto documentCategoryDto = null;
		// Validate inputs
		if (files == null || files.isEmpty()) {
			throw new RuntimeException("No files provided for upload");
		}

		if (files.size() > 10) { // Limit to prevent abuse
			throw new RuntimeException("Maximum 10 files allowed per request");
		}
		
	//	Long companyId = SecurityUtils.getCurrentUserCompanyId();
		
		DocumentType type = null;
		Profile profile = profileRepository.findById(profileId)
				.orElseThrow(() -> new RuntimeException("Profile not found: " + profileId));
		CheckCategory category = checkCategoryRepository.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));
		DocumentType documentType = documentTypeRepository.findById(typeId)
		        .orElseThrow(() -> new RuntimeException("Doctype not found: " + profileId));
		Candidate candidate = candidateRepository.findById(candidateId)
		        .orElseThrow(() -> new RuntimeException("CandidateId not found: " + candidateId));
		
		
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
				
				if(category.getName().contains("Identity")) {
					
					IdentityProof identityProof = IdentityProof.builder().profile(profile).build();
					identityProof = identityProofRepository.save(identityProof);
					
					Document document = Document.builder().profile(profile).category(category).docTypeId(documentType)
							.fileUrl(uploadResult.getFirst()).fileSize(file.getSize()).awsDocKey(uploadResult.getSecond())
							.status("UPLOADED").uploadedAt(LocalDateTime.now())
							.objectId(identityProof.getId())
							//.companyId(companyId)
							.candidate(candidate)
							.entityType("Identity")
							.build();
					Document saved = documentRepository.save(document);
					//  successfulUploads.add(convertToDocumentResponse(document));
					 documentCategoryDto = getDocumentsBySection(profileId, "Identity");
				}else if (category.getName().contains("Education")) {
					/*
					EducationDocuments document = EducationDocuments.builder().profile(profile).category(category).docTypeId(documentType)
							.fileUrl(uploadResult.getFirst()).fileSize(file.getSize()).awsDocKey(uploadResult.getSecond())
							.status("UPLOADED").uploadedAt(LocalDateTime.now())
							.objectId(objectId)
							.build();
					EducationDocuments saved = educationDocumentsRepository.save(document);
					successfulUploads.add(convertToDocumentResponse(document));
					*/
					Document document = Document.builder().profile(profile).category(category).docTypeId(documentType)
							.fileUrl(uploadResult.getFirst()).fileSize(file.getSize()).awsDocKey(uploadResult.getSecond())
							.status("UPLOADED").uploadedAt(LocalDateTime.now())
							.objectId(objectId)
							//.companyId(companyId)
							.candidate(candidate)
							.entityType("Education")
							.build();
					Document saved = documentRepository.save(document);
					documentCategoryDto = getDocumentsBySection(profileId, "Education");
				}else if (category.getName().contains("Work Experience")) {
					/*
					ProfessionalDocuments document = ProfessionalDocuments.builder().profile(profile).category(category).docTypeId(documentType)
							.fileUrl(uploadResult.getFirst()).fileSize(file.getSize()).awsDocKey(uploadResult.getSecond())
							.status("UPLOADED").uploadedAt(LocalDateTime.now())
							.objectId(objectId)
							.build();
					ProfessionalDocuments saved = professionalDocumentsRepository.save(document);
					successfulUploads.add(convertToDocumentResponse(document));
					*/
					Document document = Document.builder().profile(profile).category(category).docTypeId(documentType)
							.fileUrl(uploadResult.getFirst()).fileSize(file.getSize()).awsDocKey(uploadResult.getSecond())
							.status("UPLOADED").uploadedAt(LocalDateTime.now())
							.objectId(objectId)
							//.companyId(companyId)
							.candidate(candidate)
							.entityType("Work Experience")
							.build();
					Document saved = documentRepository.save(document);
					documentCategoryDto = getDocumentsBySection(profileId, "Work Experience");
				}else {
					Document document = Document.builder().profile(profile).category(category).docTypeId(documentType)
							.fileUrl(uploadResult.getFirst()).fileSize(file.getSize()).awsDocKey(uploadResult.getSecond())
							.status("UPLOADED").uploadedAt(LocalDateTime.now())
							.objectId(objectId)
							//.companyId(companyId)
							.candidate(candidate)
							.entityType("Other")
							.build();
					Document saved = documentRepository.save(document);
					successfulUploads.add(convertToDocumentResponse(document));
					documentCategoryDto = getDocumentsBySection(profileId, "Other");
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

		return documentCategoryDto;
	}
	
	@Transactional
    public DeleteResponse deleteDocument(Long docId) {
    	
    	logger.info("documentservice:::::::::{}",docId);
    	try {
    	if(docId!=null) {
    		Document document = documentRepository.findById(docId)
	                .orElseThrow(() -> new RuntimeException("Document not found: " + docId));
			 deleteDocfromS3(document.getAwsDocKey());
			 documentRepository.delete(document);
			 
    	}
    	}catch (Exception e) {
			logger.error(e.getMessage());
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
        List<CheckCategory> categories = checkCategoryRepository.findAll();
        
        for (CheckCategory category : categories) {
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
                option.put("documentTypeId", docType.getDocTypeId());
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
    
    
    public List<CheckCategoryGroup> getDocumentsByProfileGroupedByCategory(Long profileId) {
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
            
            Map<Long, CheckCategory> categoriesMap = checkCategoryRepository.findAllById(categoryIds)
                    .stream()
                    .collect(Collectors.toMap(CheckCategory::getCategoryId, Function.identity()));

            // Group documents by category with proper error handling
            Map<CheckCategory, List<BaseDocument>> groupedByCategory = allDocuments.stream()
                    .collect(Collectors.groupingBy(
                        doc -> {
                        	CheckCategory category = doc.getCategory();
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
                    .sorted(Comparator.comparing(CheckCategoryGroup::getCategoryId))
                    .collect(Collectors.toList());

        } catch (EntityNotFoundException e) {
            throw e; // Re-throw specific exceptions
        } catch (Exception e) {
           
            throw new ServiceException("Failed to retrieve documents for profile: " + profileId, e);
        }
    }

    private CheckCategoryGroup createCategoryGroup(CheckCategory category, List<BaseDocument> documents) {
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

        return CheckCategoryGroup.builder()
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
                    .doc_id(document.getDocId())
                    .category_id(document.getCategory() != null ? document.getCategory().getCategoryId() : null)
                    .doc_type_id(document.getDocTypeId() != null ? document.getDocTypeId().getDocTypeId() : null)
                    .file_url(document.getFileUrl())
                    .file_size(document.getFileSize())
                    .status(document.getStatus())
                    .uploadedAt(document.getUploadedAt())
                    .verifiedAt(document.getVerifiedAt())
                    .comments(document.getComments())
                    .awsDocKey(document.getAwsDocKey())
                    
                    .file_name(extractFileName(document.getFileUrl()))
                    .file_type(extractFileType(document.getFileUrl()))
                    .build();
        } catch (Exception e) {
            
            return null;
        }
    }
    
    
    
    private FileDTO convertToFileDTO(BaseDocument document) {
    	try {
    		return FileDTO.builder()
    				.fileId(document.getDocId())
    				.fileName(extractFileName(document.getFileUrl()))
    				.fileSize(document.getFileSize())
    				.fileUrl(document.getFileUrl())
    				.uploadedAt(document.getUploadedAt())
    				.status(document.getStatus())
    				.fileType(extractFileType(document.getFileUrl()))
    				.build();
    		
    	}catch (Exception e) {
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

    private CheckCategory getUnknownCategory() {
        return CheckCategory.builder()
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
        Optional<CheckCategory> categoryOpt = checkCategoryRepository.findByNameContainingIgnoreCase(categoryName);
        
        if (categoryOpt.isEmpty()) {
            throw new RuntimeException("Category not found with name: " + categoryName);
        }
        
        CheckCategory category = categoryOpt.get();
        
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
                .doc_type_id(documentType.getDocTypeId())
                .name(documentType.getName())
                .categoryName(documentType.getCategory() != null ? documentType.getCategory().getName() : null)
                .build();
    }
    
    private DocumentTypeResponse convertToResponse(DocumentType documentType, CheckCategory category) {
        return DocumentTypeResponse.builder()
                .categoryId(category.getCategoryId())
                .doc_type_id(documentType.getDocTypeId())
                .name(documentType.getName())
                .categoryName(category.getName())
              //  .categoryDescription(category.getDescription())
              //  .categoryLabel(category.getLabel())
                .build();
    }
    private void deleteDocfromS3(String key) {
    	try {
    	 if (key != null) {
             s3StorageService.deleteFile(key);
             logger.info("document deleted with key:::::{}",key);
         }
    	}catch (Exception e) {
			logger.error(e.getMessage());
		}
    }
    /*
    public CategoriesDTO getDocuments(Long profileId) {
    	
    	List<DocumentCategory> documentCategories = documentCategoryRepository.findAll();
    	 List<IdentityProof> identityProofs = identityProofRepository.findByProfile_ProfileId(profileId);
    	 List<EducationHistory> educationHistories = educationHistoryRepository.findByProfile_ProfileId(profileId);
    	 List<WorkExperience> workExperiences = workExperienceRepository.findByProfile_ProfileId(profileId);
    	 List<Other> others = otherRepository.findByProfile_ProfileId(profileId);
		  List<DocumentCategoryDto> documentCategoryDtos = new ArrayList<>();
		 
		  
    	for(DocumentCategory documentCategory : documentCategories) {
    		 DocumentCategoryDto documentCategoryDto  = DocumentCategoryDto.builder().allowedFileTypes(null)
    		.categoryId(documentCategory.getCategoryId())
    		.categoryLabel(documentCategory.getLabel())
    		.categoryName(documentCategory.getName())
    		.isRequired(Boolean.TRUE)
    		.maxDocuments(null)
    		.build();
    		 
    		 List<DocumentType> documentTypes = documentTypeRepository.findByCategoryCategoryId(documentCategory.getCategoryId());
    		 
    		 if(documentCategory.getName().contains("IDENTITY_PROOF")) {
    			 
    			 List<DocumentTypeDto> documentTypeDtos = new ArrayList<>();
    	    		for (DocumentType documentType : documentTypes) {
    	    			DocumentTypeDto documentTypeDto = DocumentTypeDto.builder()
    	    			.typeId(documentType.getDocTypeId())
    	    			.typeLabel(documentType.getLabel())
    	    			.typeName(documentType.getName())
    	    			.description(null)
    	    			.isRequired(Boolean.TRUE)
    	    			.maxFiles(null)
    	    			.build();
    	    			
    	    			List<Document> docList = documentRepository.findByProfile_ProfileIdAndCategory_CategoryIdAndDocTypeId_DocTypeId(profileId, documentCategory.getCategoryId(),  documentType.getDocTypeId());
    	    			List<FileDTO> documentResponses = new ArrayList<>();
    	    			for(Document document:docList) {
    	    				documentResponses.add(convertToFileDTO(document));
    	    			}
    	    			documentTypeDto.setFiles(documentResponses);
    	    			documentTypeDtos.add(documentTypeDto);		
    	    		}
    	    		documentCategoryDto.setDocumentTypes(documentTypeDtos);
    	    		
    		 }else if(documentCategory.getName().contains("EDUCATION")) {
    			 List<DocumentTypeDto> documentTypeDtos = new ArrayList<>();
    			 List<EducationDTO> educationDTOList = new ArrayList<>();
    			 for (EducationHistory educationHistory : educationHistories) {
					
    				 EducationDTO educationDTO = EducationDTO.builder()
    				 .degreeLabel(educationHistory.getDegree().getLabel())
    				 .degreeType(educationHistory.getTypeOfEducation())
    				 .eduId(educationHistory.getId())
    				 .fieldOfStudy(null)
    				 .build();
    				 
    				 for (DocumentType documentType : documentTypes) {
     	    			DocumentTypeDto documentTypeDto = DocumentTypeDto.builder()
     	    			.typeId(documentType.getDocTypeId())
     	    			.typeLabel(documentType.getLabel())
     	    			.typeName(documentType.getName())
     	    			.description(null)
     	    			.isRequired(Boolean.TRUE)
     	    			.maxFiles(null)
     	    			.build();
     	    			
     	    			List<Document> docList = documentRepository.findByProfile_ProfileIdAndCategory_CategoryIdAndDocTypeId_DocTypeIdAndObjectId(profileId, documentCategory.getCategoryId(),  documentType.getDocTypeId(),educationHistory.getId());
     	    			List<FileDTO> documentResponses = new ArrayList<>();
     	    			for(Document document:docList) {
     	    				documentResponses.add(convertToFileDTO(document));
     	    			}
     	    			documentTypeDto.setFiles(documentResponses);
     	    			educationDTO.setDocumentTypes(documentTypeDtos);
     	    			
     	    		}
    				 educationDTOList.add(educationDTO);	
				}
    			 documentCategoryDto.setEducation(educationDTOList);
    			 
    		 }else if(documentCategory.getName().contains("WORK_EXPERIENCE")) {
    			 List<DocumentTypeDto> documentTypeDtos = new ArrayList<>();
    			 List<CompanyDto> companyDtos = new ArrayList<>();
    			 for (WorkExperience workExperience : workExperiences) {
					CompanyDto companyDto = CompanyDto.builder()
							.companyId(workExperience.getExperienceId())
							.companyName(workExperience.getCompany_name())
							.build();
					
					for (DocumentType documentType : documentTypes) {
     	    			DocumentTypeDto documentTypeDto = DocumentTypeDto.builder()
     	    			.typeId(documentType.getDocTypeId())
     	    			.typeLabel(documentType.getLabel())
     	    			.typeName(documentType.getName())
     	    			.description(null)
     	    			.isRequired(Boolean.TRUE)
     	    			.maxFiles(null)
     	    			.build();
     	    			
     	    			List<Document> docList = documentRepository.findByProfile_ProfileIdAndCategory_CategoryIdAndDocTypeId_DocTypeIdAndObjectId(profileId, documentCategory.getCategoryId(),  documentType.getDocTypeId(),workExperience.getExperienceId());
     	    			List<FileDTO> documentResponses = new ArrayList<>();
     	    			for(Document document:docList) {
     	    				documentResponses.add(convertToFileDTO(document));
     	    			}
     	    			documentTypeDto.setFiles(documentResponses);
     	    			
     	    			 documentTypeDtos.add(documentTypeDto);		
     	    		}
					
					companyDto.setDocumentTypes(documentTypeDtos);
					companyDtos.add(companyDto);
				}
    			 documentCategoryDto.setCompanies(companyDtos);
    		 }else {
    			
    			 List<DocumentTypeDto> documentTypeDtos = new ArrayList<>();
    			 
    			 for (DocumentType documentType : documentTypes) {
  	    			DocumentTypeDto documentTypeDto = DocumentTypeDto.builder()
  	    			.typeId(documentType.getDocTypeId())
  	    			.typeLabel(documentType.getLabel())
  	    			.typeName(documentType.getName())
  	    			.description(null)
  	    			.isRequired(Boolean.TRUE)
  	    			.maxFiles(null)
  	    			.build();
  	    			
  	    			List<Document> docList = documentRepository.findByProfile_ProfileIdAndCategory_CategoryIdAndDocTypeId_DocTypeId(profileId, documentCategory.getCategoryId(),  documentType.getDocTypeId());
  	    			List<FileDTO> documentResponses = new ArrayList<>();
  	    			for(Document document:docList) {
  	    				documentResponses.add(convertToFileDTO(document));
  	    			}
  	    			documentTypeDto.setFiles(documentResponses);
  	    			
  	    			 documentTypeDtos.add(documentTypeDto);		
  	    		}
    			 documentCategoryDto.setDocumentTypes(documentTypeDtos);
    			 
    		 }
    		 documentCategoryDtos.add(documentCategoryDto);
    	}
    	
    	return CategoriesDTO.builder().categories(documentCategoryDtos).build();
    	
    }
    
    
    */
    public CategoriesDTO getDocuments(Long profileId) {
        List<CheckCategory> documentCategories = checkCategoryRepository.findAll();
        List<DocumentCategoryDto> documentCategoryDtos = new ArrayList<>();
        
        for (CheckCategory documentCategory : documentCategories) {
            DocumentCategoryDto categoryDto = buildDocumentCategoryDto(documentCategory);
            List<DocumentType> documentTypes = documentTypeRepository.findByCategoryCategoryId(documentCategory.getCategoryId());
            
            switch (documentCategory.getName()) {
                case "IDENTITY_PROOF":
                    categoryDto.setDocumentTypes(buildIdentityProofDocumentTypes(profileId, documentCategory, documentTypes));
                    break;
                    
                case "EDUCATION":
                    categoryDto.setEducation(buildEducationDocuments(profileId, documentCategory, documentTypes));
                    break;
                    
                case "WORK_EXPERIENCE":
                    categoryDto.setCompanies(buildWorkExperienceDocuments(profileId, documentCategory, documentTypes));
                    break;
                    
                default:
                    categoryDto.setDocumentTypes(buildGenericDocumentTypes(profileId, documentCategory, documentTypes));
                    break;
            }
            
            documentCategoryDtos.add(categoryDto);
        }
        
        
        
        return CategoriesDTO.builder().categories(documentCategoryDtos).build();
    }
    
    public DocumentCategoryDto getDocumentsBySection(Long profileId, String section) {
      
    	// Find category by section name (case insensitive)
        Optional<CheckCategory> documentCategoryOpt = checkCategoryRepository.findByNameIgnoreCase(section);
        CheckCategory documentCategory = documentCategoryOpt.get();
        logger.info("Section:::::::::::::{}",section);
        
        if (documentCategoryOpt.isEmpty()) {
            throw new RuntimeException("Section not found: " + section.length());
        }
        List<DocumentType> candidatedocumentTypes = new ArrayList<>();
       Long companyId = SecurityUtils.getCurrentUserCompanyId();
       Long userId = SecurityUtils.getCurrentUserId();
       Candidate candidate = candidateRepository.findByCompanyIdAndUserUserId(companyId, userId);
       
       VerificationCase verificationCase = verificationCaseService.getVerificationCaseByCompanyIdAndCandidateIdAndStatus(companyId, candidate.getCandidateId(), CaseStatus.CREATED);
       logger.info("!!!!!!!!!!!!!!!!!!!!::verificationCase::{}",verificationCase);
       if(verificationCase!=null) {
        	VerificationCaseCheck verificationCaseCheck = verificationCaseService.getVerificationCaseChackByCategory(companyId, verificationCase.getCaseId(), section);
        	
        	
        	logger.info("11111111::::::::::::companyId:::{}{}{}",companyId, userId, section);
        	logger.info("11111111::::::::::::companyId:::{}",documentCategory.getCategoryId());
        	// findByVerificationCaseCaseIdAndCheckCategoryCategoryId
        	List<VerificationCaseDocument> verificationCaseDocuments = verificationCaseService.getVerificationCaseCaseIdAndCheckCategoryCategoryId(verificationCase.getCaseId(), documentCategory.getCategoryId());
        	candidatedocumentTypes = verificationCaseDocuments.stream().map(m->m.getDocumentType()).collect(Collectors.toList());
        	logger.info("22222222222:::::::::::::::{}",verificationCaseDocuments);
        }
       
        DocumentCategoryDto categoryDto = buildDocumentCategoryDto(documentCategory);
        List<DocumentType> documentTypes = documentTypeRepository.findByCategoryCategoryId(documentCategory.getCategoryId());
        
        logger.info("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&:::candidatedocumentTypes:::::::{}",candidatedocumentTypes);
        
        if(candidatedocumentTypes!=null && candidatedocumentTypes.size()>0) {
        	documentTypes = candidatedocumentTypes;
        }
        
        logger.info("document service ::::::::documentTypes"+documentTypes.size());
        
        switch (documentCategory.getName().toUpperCase()) {
            case "Identity":
                categoryDto.setDocumentTypes(buildIdentityProofDocumentTypes(profileId, documentCategory, documentTypes));
                break;
                
            case "Education":
                categoryDto.setEducation(buildEducationDocuments(profileId, documentCategory, documentTypes));
                break;
                
            case "Work Experience":
                categoryDto.setCompanies(buildWorkExperienceDocuments(profileId, documentCategory, documentTypes));
                break;
                
            default:
                categoryDto.setDocumentTypes(buildGenericDocumentTypes(profileId, documentCategory, documentTypes));
                break;
        }
        
        return categoryDto;
    }
         
    	
    	
    
    
    // Generic Document Category DTO Builder
    private DocumentCategoryDto buildDocumentCategoryDto(CheckCategory category) {
        return DocumentCategoryDto.builder()
            .categoryId(category.getCategoryId())
            .categoryLabel(category.getLabel())
            .categoryName(category.getName())
            .isRequired(Boolean.TRUE)
            .maxDocuments(null)
            .allowedFileTypes(null)
            .maxFileSize(null)
            .build();
    }
    
    // Generic Document Type DTO Builder
    private DocumentTypeDto buildDocumentTypeDto(DocumentType documentType) {
        return DocumentTypeDto.builder()
            .typeId(documentType.getDocTypeId())
            .typeLabel(documentType.getLabel())
            .typeName(documentType.getName())
            .description(null)
            .isRequired(Boolean.TRUE)
            .maxFiles(null)
            .customTypeName(null)
            .build();
    }
    
   
    
    // Identity Proof Documents
    private List<DocumentTypeDto> buildIdentityProofDocumentTypes(Long profileId, 
    		CheckCategory category, 
                                                                 List<DocumentType> documentTypes) {
        return documentTypes.stream()
            .map(documentType -> {
                DocumentTypeDto dto = buildDocumentTypeDto(documentType);
                if(documentType!=null && documentType.getName().equalsIgnoreCase("AADHAR")) {
                	dto.setFields(createAadharFields());
                }else if(documentType!=null && documentType.getName().equalsIgnoreCase("PANCARD")) {
                	dto.setFields(createPanFields());
                }else if(documentType!=null && documentType.getName().equalsIgnoreCase("PASSPORT")) {
                	dto.setFields(createPassportFields());
                }
                
                
                List<Document> documents = documentRepository.findByProfile_ProfileIdAndCategory_CategoryIdAndDocTypeId_DocTypeId(
                    profileId, category.getCategoryId(), documentType.getDocTypeId());
                dto.setFiles(convertDocumentsToFileDTOs(documents));
                return dto;
            })
            .collect(Collectors.toList());
    }
    
    // Education Documents
    private List<EducationDTO> buildEducationDocuments(Long profileId, 
    		CheckCategory category, 
                                                      List<DocumentType> documentTypes) {
        List<EducationHistory> educationHistories = educationHistoryRepository.findByProfile_ProfileId(profileId);
        
        return educationHistories.stream()
            .map(education -> {
                EducationDTO educationDTO = EducationDTO.builder()
                    .eduId(education.getId())
                    .degreeLabel(education.getDegree().getLabel())
                    .degreeType(education.getTypeOfEducation())
                    .fieldOfStudy(education.getField().getName())
                    .institionName(education.getInstitute_name())
                    .build();
                
                List<DocumentTypeDto> eduDocumentTypes = buildEducationDocumentTypes(profileId, category, documentTypes, education.getId());
                educationDTO.setDocumentTypes(eduDocumentTypes);
                
                return educationDTO;
            })
            .collect(Collectors.toList());
    }
    
    // Education Document Types
    private List<DocumentTypeDto> buildEducationDocumentTypes(Long profileId, 
    		                                                 CheckCategory category, 
                                                             List<DocumentType> documentTypes, 
                                                             Long educationId) {
        return documentTypes.stream()
            .map(documentType -> {
                DocumentTypeDto dto = buildDocumentTypeDto(documentType);
                List<Document> documents = documentRepository.findByProfile_ProfileIdAndCategory_CategoryIdAndDocTypeId_DocTypeIdAndObjectId(
                    profileId, category.getCategoryId(), documentType.getDocTypeId(), educationId);
                dto.setFiles(convertDocumentsToFileDTOs(documents));
                return dto;
            })
            .collect(Collectors.toList());
    }
    
    // Work Experience Documents
    private List<CompanyDto> buildWorkExperienceDocuments(Long profileId, 
    		CheckCategory category, 
                                                         List<DocumentType> documentTypes) {
        List<WorkExperience> workExperiences = workExperienceRepository.findByProfile_ProfileId(profileId);
        
        return workExperiences.stream()
            .map(workExperience -> {
                CompanyDto companyDto = CompanyDto.builder()
                    .companyId(workExperience.getExperienceId())
                    .companyName(workExperience.getCompany_name())
                    .build();
                
                List<DocumentTypeDto> companyDocumentTypes = buildCompanyDocumentTypes(profileId, category, documentTypes, workExperience.getExperienceId());
                companyDto.setDocumentTypes(companyDocumentTypes);
                
                return companyDto;
            })
            .collect(Collectors.toList());
    }
    
    // Company Document Types
    private List<DocumentTypeDto> buildCompanyDocumentTypes(Long profileId, 
    		CheckCategory category, 
                                                           List<DocumentType> documentTypes, 
                                                           Long companyId) {
        return documentTypes.stream()
            .map(documentType -> {
                DocumentTypeDto dto = buildDocumentTypeDto(documentType);
                List<Document> documents = documentRepository.findByProfile_ProfileIdAndCategory_CategoryIdAndDocTypeId_DocTypeIdAndObjectId(
                    profileId, category.getCategoryId(), documentType.getDocTypeId(), companyId);
                dto.setFiles(convertDocumentsToFileDTOs(documents));
                return dto;
            })
            .collect(Collectors.toList());
    }
    
    // Generic Document Types (for OTHER category)
    private List<DocumentTypeDto> buildGenericDocumentTypes(Long profileId, 
    		CheckCategory category, 
                                                           List<DocumentType> documentTypes) {
        return documentTypes.stream()
            .map(documentType -> {
                DocumentTypeDto dto = buildDocumentTypeDto(documentType);
                List<Document> documents = documentRepository.findByProfile_ProfileIdAndCategory_CategoryIdAndDocTypeId_DocTypeId(
                    profileId, category.getCategoryId(), documentType.getDocTypeId());
                dto.setFiles(convertDocumentsToFileDTOs(documents));
                return dto;
            })
            .collect(Collectors.toList());
    }
    
    // Generic Document to FileDTO converter
    private List<FileDTO> convertDocumentsToFileDTOs(List<Document> documents) {
        return documents.stream()
            .map(this::convertToFileDTO)
            .collect(Collectors.toList());
    }
    
    private static List<FieldDTO> createAadharFields() {
        return List.of(
            FieldDTO.builder()
                    .name("documentNumber")
                    .label("Aadhar Number")
                    .type("text")
                    .required(true)
                    .value("") // Empty for new entry
                    .build(),
            FieldDTO.builder()
                    .name("issueDate")
                    .label("Issue Date")
                    .type("date")
                    .required(false)
                    .value("")
                    .build()
        );
    }
    
    private static List<FieldDTO> createPanFields() {
        return List.of(
            FieldDTO.builder()
                    .name("documentNumber")
                    .label("PAN Number")
                    .type("text")
                    .required(true)
                    .value("")
                    .build(),
            FieldDTO.builder()
                    .name("issueDate")
                    .label("Issue Date")
                    .type("date")
                    .required(false)
                    .value("")
                    .build()
        );
    }
    
    private static List<FieldDTO> createPassportFields() {
        return List.of(
            FieldDTO.builder()
                    .name("documentNumber")
                    .label("Passport Number")
                    .type("text")
                    .required(true)
                    .value("")
                    .build(),
            FieldDTO.builder()
                    .name("issueDate")
                    .label("Issue Date")
                    .type("date")
                    .required(true)
                    .value("")
                    .build(),
            FieldDTO.builder()
                    .name("expiryDate")
                    .label("Expiry Date")
                    .type("date")
                    .required(true)
                    .value("")
                    .build()
        );
    }
    
}
