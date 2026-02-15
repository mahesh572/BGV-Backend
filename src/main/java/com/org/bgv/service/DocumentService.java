package com.org.bgv.service;

import com.org.bgv.candidate.dto.CandidateActionCatalog;
import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.candidate.entity.EducationHistory;
import com.org.bgv.candidate.entity.IdentityProof;
import com.org.bgv.candidate.entity.WorkExperience;
import com.org.bgv.candidate.repository.CandidateRepository;
import com.org.bgv.candidate.repository.EducationHistoryRepository;
import com.org.bgv.candidate.repository.IdentityProofRepository;
import com.org.bgv.candidate.repository.WorkExperienceRepository;
import com.org.bgv.common.DocumentEntityType;
import com.org.bgv.common.DocumentStatus;
import com.org.bgv.common.EvidenceLevel;
import com.org.bgv.common.Status;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.constants.SectionConstants;
import com.org.bgv.constants.VerificationStatus;
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
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.Document;
import com.org.bgv.entity.DocumentType;
//import com.org.bgv.entity.EducationDocuments;
import com.org.bgv.entity.IdentityDocuments;
import com.org.bgv.entity.Other;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.entity.VerificationCaseDocument;
import com.org.bgv.entity.VerificationCaseDocumentLink;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.repository.DocumentRepository;
import com.org.bgv.repository.DocumentTypeRepository;
import com.org.bgv.repository.IdentityDocumentsRepository;
import com.org.bgv.repository.OtherRepository;
import com.org.bgv.repository.ProfileRepository;
import com.org.bgv.repository.VerificationCaseCheckRepository;
import com.org.bgv.repository.VerificationCaseDocumentLinkRepository;
import com.org.bgv.repository.VerificationCaseDocumentRepository;
import com.org.bgv.repository.VerificationCaseRepository;
import com.org.bgv.s3.S3StorageService;
import com.org.bgv.vendor.action.dto.ActionDTO;
import com.org.bgv.vendor.dto.ActionLevel;
import com.org.bgv.vendor.dto.ActionStatus;
import com.org.bgv.vendor.dto.ActionType;
import com.org.bgv.vendor.entity.CategoryEvidenceType;
import com.org.bgv.vendor.entity.EvidenceType;
import com.org.bgv.vendor.entity.VerificationAction;
import com.org.bgv.vendor.repository.CategoryEvidenceTypeRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
public class DocumentService {

    private final DocumentRepository documentRepository;
   // private final ProfessionalDocumentsRepository professionalDocumentsRepository;
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
    private final VerificationCaseRepository verificationCaseRepository;
    private final VerificationCaseDocumentRepository verificationCaseDocumentRepository;
    private final VerificationCaseDocumentLinkRepository verificationCaseDocumentLinkRepository;
    private final VerificationCaseCheckRepository verificationCaseCheckRepository;
    private final CategoryEvidenceTypeRepository categoryEvidenceTypeRepository;
   // private final VerificationEvidenceRepository verificationEvidenceRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    public DocumentCategoryDto createDocuments(
            List<MultipartFile> files,
            Long candidateId,
            Long categoryId,
            Long typeId,
            Long objectId,
            Long caseId,
            Long checkId,
            String action,
            Long oldDocId) {

        validateFiles(files);

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("CandidateId not found: " + candidateId));

        CheckCategory category = checkCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found: " + categoryId));

        DocumentType documentType = documentTypeRepository.findById(typeId)
                .orElseThrow(() -> new RuntimeException("Doctype not found: " + typeId));

        SectionConstants section =
                SectionConstants.fromNameOrValue(category.getName());
        
        Document oldDocument = null;
        DocumentStatus status = DocumentStatus.UPLOADED;

        if ("REUPLOAD".equalsIgnoreCase(action)) {
            oldDocument = handleReupload(
                    candidate,
                    documentType,
                    caseId,
                    checkId,
                    oldDocId
            );
            
            status = DocumentStatus.RE_UPLOADED;
        }

        for (MultipartFile file : files) {
            uploadSingleFile(file, candidate, category, documentType, section, objectId,caseId,checkId,status);
        }

        return getDocumentsBySection(candidateId,caseId, section.getValue(),caseId,checkId);
    }
    
    private void validateFiles(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new RuntimeException("No files provided for upload");
        }
        if (files.size() > 10) {
            throw new RuntimeException("Maximum 10 files allowed per request");
        }
    }
    
    private void uploadSingleFile(
            MultipartFile file,
            Candidate candidate,
            CheckCategory category,
            DocumentType documentType,
            SectionConstants section,
            Long objectId,
            Long caseId,
            Long checkId,
            DocumentStatus status
    ) {
        log.info(
            "Document upload started | candidateId={} | caseId={} | checkId={} | section={} | docType={}",
            candidate.getCandidateId(),
            caseId,
            checkId,
            section.name(),
            documentType.getLabel()
        );

        if (file.isEmpty()) {
            log.error("Upload failed: empty file | fileName={}", file.getOriginalFilename());
            throw new RuntimeException("Empty file: " + file.getOriginalFilename());
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            log.error(
                "Upload failed: file too large | fileName={} | size={} bytes",
                file.getOriginalFilename(),
                file.getSize()
            );
            throw new RuntimeException("File too large: " + file.getOriginalFilename());
        }

        VerificationCase verificationCase = null;
        VerificationCaseCheck verificationCaseCheck = null;

        // =========================
        // CASE VERIFICATION MODE
        // =========================
        if (caseId != null && caseId > 0) {
            log.debug("Resolving verification case | caseId={}", caseId);

            verificationCase = verificationCaseRepository.findById(caseId)
                    .orElseThrow(() -> {
                        log.error("Verification case not found | caseId={}", caseId);
                        return new RuntimeException("Verification case not found: " + caseId);
                    });

            verificationCaseCheck =
                    verificationCaseCheckRepository
                            .findByVerificationCase_CaseIdAndCaseCheckId(
                                    verificationCase.getCaseId(),
                                    checkId
                            );

            if (verificationCaseCheck == null) {
                log.error(
                    "VerificationCaseCheck not found | caseId={} | checkId={}",
                    caseId,
                    checkId
                );
                throw new RuntimeException("Verification check not found");
            }

            log.debug(
                "Resolved verification check | caseCheckId={} | category={}",
                verificationCaseCheck.getCaseCheckId(),
                verificationCaseCheck.getCategory().getName()
            );
        } else {
            log.debug("Self-profile upload mode (no case)");
        }

        // =========================
        // S3 UPLOAD
        // =========================
        log.info(
            "Uploading file to storage | fileName={} | section={}",
            file.getOriginalFilename(),
            section.getValue()
        );

        Pair<String, String> upload =
                s3StorageService.uploadFile(file, section.getValue());

        log.debug(
            "File uploaded to S3 | url={} | awsKey={}",
            upload.getFirst(),
            upload.getSecond()
        );

        // =========================
        // SAVE DOCUMENT
        // =========================
        Document document = Document.builder()
                .candidate(candidate)
                .verificationCase(verificationCase)              // null for self
                .verificationCaseCheck(verificationCaseCheck)    // null for self
                .category(category)
                .docTypeId(documentType)
                .originalFileName(file.getOriginalFilename())
                .fileUrl(upload.getFirst())
                .awsDocKey(upload.getSecond())
                .fileSize(file.getSize())
                .status(status)
                .uploadedAt(LocalDateTime.now())
                .objectId(objectId)
                .entityType(DocumentEntityType.PRIMARY)
                .uploadedBy(SecurityUtils.getCurrentCustomUserDetails().getUserType())
                .build();

        document = documentRepository.save(document);

        log.info(
            "Document saved | docId={} | candidateId={} | caseId={}",
            document.getDocId(),
            candidate.getCandidateId(),
            caseId
        );

        // =========================
        // STORE IN CASE TABLES
        // =========================
        if (verificationCase != null) {
            log.info(
                "Linking document to verification case | docId={} | caseId={} | checkId={}",
                document.getDocId(),
                verificationCase.getCaseId(),
                verificationCaseCheck.getCaseCheckId()
            );

            storeDocumentInCaseTables(
                    document,
                    category,
                    documentType,
                    verificationCase,
                    verificationCaseCheck
            );

            log.info(
                "Document linked successfully | docId={} | caseId={}",
                document.getDocId(),
                verificationCase.getCaseId()
            );
        } else {
            log.info(
                "Self-profile document upload completed | docId={} | candidateId={}",
                document.getDocId(),
                candidate.getCandidateId()
            );
        }
    }
    

    @Transactional
    public DeleteResponse deleteDocument(Long docId) {
        logger.info("documentservice:::::::::{}", docId);
        
        try {
            if (docId != null) {
                // 1. Find the document
                Document document = documentRepository.findById(docId)
                        .orElseThrow(() -> new RuntimeException("Document not found: " + docId));
                
                // 2. Find all VerificationCaseDocumentLinks for this document
                List<VerificationCaseDocumentLink> documentLinks = 
                        verificationCaseDocumentLinkRepository.findByDocument_DocId(docId);
                
                // 3. Soft delete all document links (update status to DELETED)
                for (VerificationCaseDocumentLink link : documentLinks) {
                    link.setStatus(DocumentStatus.DELETED);
                    verificationCaseDocumentLinkRepository.save(link);
                    
                    // 4. Optionally, update VerificationCaseDocument status if needed
                    updateVerificationCaseDocumentStatus(link.getCaseDocument());
                }
                
                // 5. Soft delete the document (instead of hard delete)
                document.setStatus(DocumentStatus.DELETED);
                document.setDeletedAt(LocalDateTime.now());
                documentRepository.save(document);
                
                // 6. Delete from S3 (optional - you might want to keep in S3 for audit)
                // deleteDocfromS3(document.getAwsDocKey());
                
                logger.info("Document {} soft deleted successfully", docId);
            }
        } catch (Exception e) {
            logger.error("Error deleting document: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete document: " + e.getMessage());
        }
        
        return new DeleteResponse(docId, "Document soft deleted successfully");
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

        VerificationAction action = document.getLastAction();

        return FileDTO.builder()
                .fileId(document.getDocId())
                .fileName(extractFileName(document.getFileUrl()))
                .fileSize(document.getFileSize())
                .fileUrl(document.getFileUrl())
                .uploadedAt(document.getUploadedAt())
                .status(document.getStatus())
                .actionRemarks(
                        action != null ? action.getRemarks() : null
                )
                .fileType(extractFileType(document.getFileUrl()))
                .actions(CandidateActionCatalog.documentActions(document.getStatus()))
                .build();
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
    /*
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
    */
    public DocumentCategoryDto getDocumentsBySection(
            Long candidateId,
            Long caseId,
            String section,
            Long categoryId,
            Long checkId
    ) {

        Long companyId = SecurityUtils.getCurrentUserCompanyId();

        log.info(
            "Fetching documents by section | candidateId={} | caseId={} | checkId={} | section={}",
            candidateId, caseId, checkId, section
        );

        // =========================
        // 1️⃣ Resolve Candidate
        // =========================
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found: " + candidateId));

        // =========================
        // 2️⃣ Resolve Verification Case (CASE MODE ONLY)
        // =========================
        VerificationCase verificationCase = null;
        VerificationCaseCheck verificationCaseCheck = null;
        CheckCategory category;

        if (caseId != null && caseId > 0) {

            verificationCase =
                    verificationCaseRepository
                            .findByCaseIdAndCandidateId(caseId, candidateId)
                            .orElseThrow(() ->
                                    new RuntimeException("Verification case not found: " + caseId));

            if (!verificationCase.getCompanyId().equals(companyId)) {
                throw new RuntimeException("Unauthorized access to verification case");
            }

            log.debug("Verification case resolved | caseId={}", verificationCase.getCaseId());

            // =========================
            // 3️⃣ Resolve Case Check (USING checkId)
            // =========================
            if (checkId != null) {
                verificationCaseCheck =
                        verificationCaseCheckRepository
                                .findByVerificationCase_CaseIdAndCaseCheckId(
                                        verificationCase.getCaseId(),
                                        checkId
                                );
                                

                category = verificationCaseCheck.getCategory();

                log.debug(
                    "Verification check resolved | checkId={} | category={}",
                    verificationCaseCheck.getCaseCheckId(),
                    category.getName()
                );
            } else {
                throw new RuntimeException("checkId is required in case verification mode");
            }

        } else {
            // =========================
            // SELF PROFILE MODE
            // =========================
            category =
                    checkCategoryRepository
                            .findByNameIgnoreCase(section)
                            .orElseThrow(() ->
                                    new RuntimeException("Section not found: " + section));

            log.debug("Self profile mode | category={}", category.getName());
        }

        // =========================
        // 4️⃣ Resolve Document Types
        // =========================
        List<DocumentType> documentTypes;

        if (verificationCaseCheck != null) {
            // CASE MODE → use verification_case_document
            List<VerificationCaseDocument> caseDocuments =
                    verificationCaseDocumentRepository
                            .findByVerificationCase_CaseIdAndVerificationCaseCheck_CaseCheckId(
                                    verificationCase.getCaseId(),
                                    verificationCaseCheck.getCaseCheckId()
                            );
            log.info(
                    "Case document  resolved | count={}",
                    caseDocuments.size()
                );
            documentTypes = caseDocuments.stream()
                    .map(VerificationCaseDocument::getDocumentType)
                    .distinct()
                    .toList();

            log.info(
                "Case document types resolved | count={}",
                documentTypes.size()
            );

        } else {
            // SELF MODE → use master document types
            documentTypes =
                    documentTypeRepository
                            .findByCategoryCategoryId(category.getCategoryId());

            log.debug(
                "Self document types resolved | count={}",
                documentTypes.size()
            );
        }

        // =========================
        // 5️⃣ Build Category DTO
        // =========================
        DocumentCategoryDto categoryDto = buildDocumentCategoryDto(category);

        switch (category.getName()) {

            case "Identity" ->
                    categoryDto.setDocumentTypes(
                            buildIdentityProofDocumentTypes(
                                    candidateId,
                                    verificationCase,
                                    companyId,
                                    category,
                                    documentTypes,
                                    verificationCaseCheck
                            )
                    );

            case "Education" ->
                    categoryDto.setEducation(
                            buildEducationDocuments(candidateId, category, documentTypes)
                    );

            case "Work Experience" ->
                    categoryDto.setCompanies(
                            buildWorkExperienceDocuments(candidateId, category, documentTypes)
                    );

            default ->
                    categoryDto.setDocumentTypes(
                            buildGenericDocumentTypes(candidateId, category, documentTypes)
                    );
        }

        log.info(
            "Documents resolved successfully | category={} | types={}",
            category.getName(),
            documentTypes.size()
        );

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
            .maxFiles(documentType.getMaxFiles())
            .customTypeName(null)
            .build();
    }
    
   
    // Identiy Proof
    private List<DocumentTypeDto> buildIdentityProofDocumentTypes(
            Long candidateId,
            VerificationCase verificationCase,
            Long companyId,
            CheckCategory category,
            List<DocumentType> documentTypes,
            VerificationCaseCheck verificationCaseCheck
    ) {

        boolean isSelfProfile = (verificationCase == null || verificationCaseCheck == null);

        log.info(
                "Building Identity Proof documents | candidateId={} | mode={} | category={}",
                candidateId,
                isSelfProfile ? "SELF_PROFILE" : "CASE_VERIFICATION",
                category.getName()
        );

        return documentTypes.stream().map(documentType -> {

            log.debug(
                    "Processing Identity documentType={} | candidateId={}",
                    documentType.getName(),
                    candidateId
            );

            Optional<IdentityProof> identityProofOpt;

            // =========================
            // SELF PROFILE MODE
            // =========================
            if (isSelfProfile) {

                log.debug(
                        "Fetching IdentityProof (SELF) | candidateId={} | docTypeId={}",
                        candidateId,
                        documentType.getDocTypeId()
                );

                identityProofOpt =
                        identityProofRepository
                                .findByCandidate_CandidateIdAndDocTypeId(
                                        candidateId,
                                        documentType.getDocTypeId()
                                );
            }
            // =========================
            // CASE VERIFICATION MODE
            // =========================
            else {

                log.debug(
                        "Fetching IdentityProof (CASE) | candidateId={} | caseId={} | checkId={} | docTypeId={}",
                        candidateId,
                        verificationCase.getCaseId(),
                        verificationCaseCheck.getCaseCheckId(),
                        documentType.getDocTypeId()
                );

                identityProofOpt =
                        identityProofRepository
                                .findByCandidate_CandidateIdAndDocTypeIdAndVerificationCaseAndVerificationCaseCheck(
                                        candidateId,
                                        documentType.getDocTypeId(),
                                        verificationCase,
                                        verificationCaseCheck
                                );
            }

            DocumentTypeDto dto = buildDocumentTypeDto(documentType);

            // Set IdentityProof ID (objectId)
            identityProofOpt.ifPresent(identityProof -> {
                dto.setId(identityProof.getId());
                log.debug(
                        "IdentityProof found | identityProofId={} | docType={}",
                        identityProof.getId(),
                        documentType.getName()
                );
            });

            // Dynamic fields
            switch (documentType.getName().toUpperCase()) {
                case "AADHAR" -> dto.setFields(createAadharFields());
                case "PANCARD", "PAN" -> dto.setFields(createPanFields());
                case "PASSPORT" -> dto.setFields(createPassportFields());
                default -> log.debug("No dynamic fields configured for docType={}", documentType.getName());
            }

            // Fetch documents using objectId = IdentityProof.id
            List<Document> documents = identityProofOpt
                    .map(identityProof -> {
                        log.debug(
                                "Fetching documents | identityProofId={} | categoryId={}",
                                identityProof.getId(),
                                category.getCategoryId()
                        );

                        return documentRepository
                                .findByObjectIdAndCategory_CategoryIdAndStatusNot(
                                        identityProof.getId(),
                                        category.getCategoryId(),
                                        DocumentStatus.DELETED
                                );
                    })
                    .orElse(List.of());

            log.debug(
                    "Documents fetched | docType={} | count={}",
                    documentType.getName(),
                    documents.size()
            );

            dto.setFiles(convertDocumentsToFileDTOs(documents));

            return dto;

        }).toList();
    }


    
    // Education Documents
    private List<EducationDTO> buildEducationDocuments(Long candidateId, 
    		CheckCategory category, 
                                                      List<DocumentType> documentTypes) {
        List<EducationHistory> educationHistories = educationHistoryRepository.findByCandidateId(candidateId);
        
        logger.info("buildEducationDocuments::::::::::::::::::{}",educationHistories.size());
        
        return educationHistories.stream()
            .map(education -> {
                EducationDTO educationDTO = EducationDTO.builder()
                    .eduId(education.getId())
                    .degreeLabel(education.getDegree().getLabel())
                    .degreeType(education.getTypeOfEducation())
                    .fieldOfStudy(education.getField().getName())
                    .institionName(education.getInstitute_name())
                    .build();
                
                List<DocumentTypeDto> eduDocumentTypes = buildEducationDocumentTypes(candidateId, category, documentTypes, education.getId());
                educationDTO.setDocumentTypes(eduDocumentTypes);
                
                return educationDTO;
            })
            .collect(Collectors.toList());
    }
    
    // Education Document Types
    private List<DocumentTypeDto> buildEducationDocumentTypes(Long candidateId, 
    		                                                 CheckCategory category, 
                                                             List<DocumentType> documentTypes, 
                                                             Long educationId) {
    	logger.info("educationId::::::::::::::::::::::::::::::::{}",educationId);
        return documentTypes.stream()
            .map(documentType -> {
                DocumentTypeDto dto = buildDocumentTypeDto(documentType);
                List<Document> documents = documentRepository.findByCandidate_CandidateIdAndCategory_CategoryIdAndDocTypeId_DocTypeIdAndObjectId(
                    candidateId, category.getCategoryId(), documentType.getDocTypeId(), educationId);
                dto.setFiles(convertDocumentsToFileDTOs(documents));
                return dto;
            })
            .collect(Collectors.toList());
    }
    
    // Work Experience Documents
    private List<CompanyDto> buildWorkExperienceDocuments(Long candidateId, 
    		CheckCategory category, 
                                                         List<DocumentType> documentTypes) {
        List<WorkExperience> workExperiences = workExperienceRepository.findByCandidateId(candidateId);
        
        return workExperiences.stream()
            .map(workExperience -> {
                CompanyDto companyDto = CompanyDto.builder()
                    .companyId(workExperience.getExperienceId())
                    .companyName(workExperience.getCompany_name())
                    .build();
                
                List<DocumentTypeDto> companyDocumentTypes = buildCompanyDocumentTypes(candidateId, category, documentTypes, workExperience.getExperienceId());
                companyDto.setDocumentTypes(companyDocumentTypes);
                
                return companyDto;
            })
            .collect(Collectors.toList());
    }
    
    // Company Document Types
    private List<DocumentTypeDto> buildCompanyDocumentTypes(Long candidateId, 
    		CheckCategory category, 
                                                           List<DocumentType> documentTypes, 
                                                           Long companyId) {
        return documentTypes.stream()
            .map(documentType -> {
                DocumentTypeDto dto = buildDocumentTypeDto(documentType);
                List<Document> documents = documentRepository.findByCandidate_CandidateIdAndCategory_CategoryIdAndDocTypeId_DocTypeIdAndObjectId(
                		candidateId, category.getCategoryId(), documentType.getDocTypeId(), companyId);
                dto.setFiles(convertDocumentsToFileDTOs(documents));
                return dto;
            })
            .collect(Collectors.toList());
    }
    
    // Generic Document Types (for OTHER category)
    private List<DocumentTypeDto> buildGenericDocumentTypes(
            Long candidateId,
            CheckCategory category,
            List<DocumentType> documentTypes) {
      logger.info("buildGenericDocumentTypes:::::::::::");
        if (candidateId == null || category == null || documentTypes == null) {
            logger.warn("Invalid input to buildGenericDocumentTypes | candidateId={}, category={}, documentTypes={}",
                    candidateId, category, documentTypes);
            return Collections.emptyList();
        }

        return documentTypes.stream()
            .map(documentType -> {
                try {
                    DocumentTypeDto dto = buildDocumentTypeDto(documentType);

                    List<Document> documents =
                            documentRepository
                                    .findByCandidate_CandidateIdAndCategory_CategoryIdAndDocTypeId_DocTypeIdAndStatusNot(
                                            candidateId,
                                            category.getCategoryId(),
                                            documentType.getDocTypeId(),
                                            "DELETED"
                                    );

                    dto.setFiles(convertDocumentsToFileDTOs(
                            documents != null ? documents : Collections.emptyList()
                    ));

                    return dto;

                } catch (Exception ex) {
                	logger.error(
                        "Failed to build DocumentTypeDto | candidateId={}, categoryId={}, docTypeId={}",
                        candidateId,
                        category.getCategoryId(),
                        documentType.getDocTypeId(),
                        ex
                    );

                    // Fallback DTO so UI doesn't break
                    DocumentTypeDto fallback = buildDocumentTypeDto(documentType);
                    fallback.setFiles(Collections.emptyList());
                    fallback.setError(true); // optional flag
                    fallback.setErrorMessage("Failed to load documents");

                    return fallback;
                }
            })
            .collect(Collectors.toList());
    }

    
    // Generic Document to FileDTO converter
    private List<FileDTO> convertDocumentsToFileDTOs(List<Document> documents) {
        return documents.stream()
                .filter(doc -> doc.getStatus() != DocumentStatus.DELETED)
                .filter(doc -> !Boolean.FALSE.equals(doc.getActive()))
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

    private void storeDocumentInCaseTables(
            Document document,
            CheckCategory category,
            DocumentType documentType,
            VerificationCase verificationCase,
            VerificationCaseCheck verificationCaseCheck
    ) {

        log.info(
            "Storing document in case tables | docId={} | caseId={} | checkId={} | category={} | docType={}",
            document.getDocId(),
            verificationCase.getCaseId(),
            verificationCaseCheck.getCaseCheckId(),
            category.getName(),
            documentType.getLabel()
        );

        // =========================
        // 1. FIND OR CREATE CASE DOCUMENT
        // =========================
        Optional<VerificationCaseDocument> caseDocumentOpt =
                verificationCaseDocumentRepository
                        .findByVerificationCase_CaseIdAndVerificationCaseCheck_CaseCheckIdAndDocumentType_DocTypeId(
                                verificationCase.getCaseId(),
                                verificationCaseCheck.getCaseCheckId(),
                                documentType.getDocTypeId()
                        );

        VerificationCaseDocument caseDocument;

        if (caseDocumentOpt.isPresent()) {
            caseDocument = caseDocumentOpt.get();

            log.debug(
                "Existing case document found | caseDocumentId={} | verificationStatus={}",
                caseDocument.getCaseDocumentId(),
                caseDocument.getVerificationStatus()
            );
        } else {
            log.info(
                "No case document found, creating new | caseId={} | checkId={} | docType={}",
                verificationCase.getCaseId(),
                verificationCaseCheck.getCaseCheckId(),
                documentType.getLabel()
            );

            caseDocument = VerificationCaseDocument.builder()
                    .verificationCase(verificationCase)
                    .verificationCaseCheck(verificationCaseCheck)
                    .checkCategory(category)
                    .documentType(documentType)
                    .isAddOn(false)
                    .required(true)
                    .documentPrice(0.0)
                    .verificationStatus(DocumentStatus.UPLOADED) 
                    .createdAt(LocalDateTime.now())
                    .build();

            caseDocument = verificationCaseDocumentRepository.save(caseDocument);
            

            log.info(
                "Case document created | caseDocumentId={} | caseId={} | checkId={}",
                caseDocument.getCaseDocumentId(),
                verificationCase.getCaseId(),
                verificationCaseCheck.getCaseCheckId()
            );
        }

        // =========================
        // 2. CREATE DOCUMENT LINK
        // =========================
        log.debug(
            "Linking document to case document | docId={} | caseDocumentId={}",
            document.getDocId(),
            caseDocument.getCaseDocumentId()
        );

        VerificationCaseDocumentLink documentLink =
                VerificationCaseDocumentLink.builder()
                        .caseDocument(caseDocument)
                        .document(document)
                        .status(DocumentStatus.UPLOADED)
                        .linkedAt(LocalDateTime.now())
                        .build();

        verificationCaseDocumentLinkRepository.save(documentLink);

        log.info(
            "Document linked successfully | docId={} | caseDocumentId={} | status={}",
            document.getDocId(),
            caseDocument.getCaseDocumentId(),
            DocumentStatus.UPLOADED
        );
    }

	
	private void updateVerificationCaseDocumentStatus(VerificationCaseDocument caseDocument) {
	    // Check if all links for this case document are deleted
	    List<VerificationCaseDocumentLink> allLinks = 
	            verificationCaseDocumentLinkRepository.findByCaseDocument_CaseDocumentId(
	                caseDocument.getCaseDocumentId());
	    
	    boolean allLinksDeleted = allLinks.stream()
	            .allMatch(link -> "DELETED".equals(link.getStatus()));
	    
	    if (allLinksDeleted) {
	        // Update VerificationCaseDocument status if all links are deleted
	        caseDocument.setVerificationStatus(DocumentStatus.NONE); // or appropriate status
	        verificationCaseDocumentRepository.save(caseDocument);
	    }
	    
	    
	    // Update VerificationCaseCheck status if needed
	    updateVerificationCaseCheckStatus(caseDocument);
	}
	
	private void updateVerificationCaseCheckStatus(VerificationCaseDocument caseDocument) {
	    Optional<VerificationCaseCheck> caseCheckOpt = verificationCaseCheckRepository
	            .findByVerificationCase_CaseIdAndCategory_CategoryId(
	                caseDocument.getVerificationCase().getCaseId(),
	                caseDocument.getCheckCategory().getCategoryId());
	    
	    if (caseCheckOpt.isPresent()) {
	        VerificationCaseCheck caseCheck = caseCheckOpt.get();
	        // Update status based on your business logic
	        caseCheck.setStatus(CaseCheckStatus.PENDING); // or appropriate status
	        caseCheck.setUpdatedAt(LocalDateTime.now());
	        verificationCaseCheckRepository.save(caseCheck);
	    }
	}
	
	
	private Document handleReupload(
	        Candidate candidate,
	        DocumentType documentType,
	        Long caseId,
	        Long checkId,
	        Long docId
	) {

	    // 1️⃣ Find old document by docId
	    Document oldDocument = documentRepository.findById(docId)
	            .orElseThrow(() ->
	                    new RuntimeException("Document not found: " + docId));

	    // 2️⃣ Validate ownership
	    if (!oldDocument.getCandidate().getCandidateId().equals(candidate.getCandidateId())) {
	        throw new RuntimeException("Document does not belong to candidate");
	    }

	    // 3️⃣ Validate case & check match
	    if (caseId != null && 
	        (oldDocument.getVerificationCase() == null ||
	         !oldDocument.getVerificationCase().getCaseId().equals(caseId))) {
	        throw new RuntimeException("Document does not belong to this case");
	    }

	    if (checkId != null && 
	        (oldDocument.getVerificationCaseCheck() == null ||
	         !oldDocument.getVerificationCaseCheck().getCaseCheckId().equals(checkId))) {
	        throw new RuntimeException("Document does not belong to this check");
	    }

	    // 4️⃣ Validate vendor action exists
	    if (oldDocument.getLastAction() == null) {
	        throw new RuntimeException("No vendor action found for this document");
	    }

	    // 5️⃣ Validate allowed statuses
	    if (!DocumentStatus.REQUEST_INFO.equals(oldDocument.getStatus())
	            && !DocumentStatus.INSUFFICIENT.equals(oldDocument.getStatus())) {
	        throw new RuntimeException("Document is not eligible for reupload");
	    }

	    // 6️⃣ Mark old document resolved
	    oldDocument.setActive(false);
	  //  oldDocument.setStatus(DocumentStatus.RESOLVED);
	  //  oldDocument.setResolvedAt(LocalDateTime.now());
	  //  oldDocument.setResolvedAction(oldDocument.getLastAction());
	    oldDocument.getLastAction().setStatus(ActionStatus.RESOLVED);
	    

	    documentRepository.save(oldDocument);

	    return oldDocument;
	}


	
}
