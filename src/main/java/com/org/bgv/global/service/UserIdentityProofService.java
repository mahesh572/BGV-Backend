package com.org.bgv.global.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.candidate.entity.IdentityProof;
import com.org.bgv.common.DocumentStatus;
import com.org.bgv.dto.DocumentUploadRequest;
import com.org.bgv.dto.FieldDTO;
import com.org.bgv.dto.IdentitySectionRequest;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.DocumentType;
import com.org.bgv.entity.User;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.global.dto.UserIdentityUpdateRequest;
import com.org.bgv.global.entity.UserIdentityProof;
import com.org.bgv.global.repository.UserIdentityProofRepository;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.repository.DocumentTypeRepository;
import com.org.bgv.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserIdentityProofService {
	
	private final CheckCategoryRepository checkCategoryRepository;
	 private final DocumentTypeRepository documentTypeRepository;
	 private final UserIdentityProofRepository userIdentityProofRepository;
	 private final UserRepository userRepository;

	 public IdentitySectionRequest createIdentitySectionResponse(Long userId) {

	        final String CATEGORY_NAME = "Identity";

	        // Fetch category
	        CheckCategory category = checkCategoryRepository
	                .findByNameIgnoreCase(CATEGORY_NAME)
	                .orElseThrow(() -> new RuntimeException("Category not found: " + CATEGORY_NAME));

	        

	        return IdentitySectionRequest.builder()
	                .section(category.getName())
	                .label(category.getName())
	                .documents(createIdentityDocuments(userId, category))
	                .build();
	    }
	
	 private List<DocumentUploadRequest> createIdentityDocuments(
	            Long userId,CheckCategory category ) {

	       

	        List<DocumentType> documentTypes =
	                documentTypeRepository.findByCategoryCategoryId(category.getCategoryId());

	        if (documentTypes.isEmpty()) {
	            return Collections.emptyList();
	        }

	        List<DocumentUploadRequest> documentList = new ArrayList<>();

	        for (DocumentType documentType : documentTypes) {

	            Optional<UserIdentityProof> identityProofOpt =
	            		userIdentityProofRepository
	                            .findByUser_UserIdAndDocTypeId(
	                                    userId,documentType.getDocTypeId()
	                            );

	            DocumentUploadRequest request =
	                    createDocumentByType(documentType, identityProofOpt.orElse(null));

	            if (request != null) {
	                documentList.add(request);
	            }
	        }

	        return documentList;
	    }
	 private DocumentUploadRequest createDocumentByType(DocumentType documentType,UserIdentityProof identityProof) {
	        switch (documentType.getName().toUpperCase()) {
	            case "AADHAR":
	                return createAadharDocument(documentType,identityProof);
	            
	            case "PANCARD":
	            case "PAN":
	                return createPanCardDocument(documentType,identityProof);
	            case "PASSPORT":
	                return createPassportDocument(documentType,identityProof);
	            
	            default:
	                // Log unknown document type instead of throwing exception
	                System.out.println("Unknown document type: " + documentType.getName());
	                return null;
	        }
	    }
	    
	    private static DocumentUploadRequest createAadharDocument(DocumentType documentType, UserIdentityProof identityProof) {
	        return DocumentUploadRequest.builder()
	                .id(identityProof!=null?identityProof.getId():null)
	        		.type("AADHAR")
	                .label("Aadhar Card")
	                .typeId(documentType.getDocTypeId())
	                .fields(createAadharFields(identityProof))
	              
	                .build();
	    }
	    
	    private static List<FieldDTO> createAadharFields(UserIdentityProof identityProof) {
	        return List.of(
	            FieldDTO.builder()
	                    .name("documentNumber")
	                    .label("Aadhar Number")
	                    .type("text")
	                    .required(true)
	                    .value(identityProof==null?"":identityProof.getDocumentNumber()) // Empty for new entry
	                    .build(),
	            FieldDTO.builder()
	                    .name("issueDate")
	                    .label("Issue Date")
	                    .type("date")
	                    .required(false)
	                    .value(identityProof==null?"":formatDateForHTML(identityProof.getIssueDate()))
	                    .build()
	        );
	    }
	    
	    private static DocumentUploadRequest createPanCardDocument(DocumentType documentType, UserIdentityProof identityProof) {
	        return DocumentUploadRequest.builder()
	                .type("PAN")
	                .label("PAN Card")
	                .typeId(documentType.getDocTypeId())
	                .fields(createPanFields(identityProof))
	               
	                .build();
	    }
	    
	    private static List<FieldDTO> createPanFields(UserIdentityProof identityProof) {
	        return List.of(
	            FieldDTO.builder()
	                    .name("documentNumber")
	                    .label("PAN Number")
	                    .type("text")
	                    .required(true)
	                    .value(identityProof==null?"":identityProof.getDocumentNumber())
	                    .build(),
	            FieldDTO.builder()
	                    .name("issueDate")
	                    .label("Issue Date")
	                    .type("date")
	                    .required(false)
	                    .value(identityProof==null?"":formatDateForHTML(identityProof.getIssueDate()))
	                    .build()
	        );
	    }
	    
	    private static DocumentUploadRequest createPassportDocument(DocumentType documentType, UserIdentityProof identityProof) {
	        return DocumentUploadRequest.builder()
	                .type("PASSPORT")
	                .label("Passport")
	                .typeId(documentType.getDocTypeId())
	                .fields(createPassportFields(identityProof))
	                
	                .build();
	    }
	    
	    private static List<FieldDTO> createPassportFields(UserIdentityProof identityProof) {
	        return List.of(
	            FieldDTO.builder()
	                    .name("documentNumber")
	                    .label("Passport Number")
	                    .type("text")
	                    .required(true)
	                    .value(identityProof==null?"":identityProof.getDocumentNumber())
	                    .build(),
	            FieldDTO.builder()
	                    .name("issueDate")
	                    .label("Issue Date")
	                    .type("date")
	                    .required(true)
	                    .value(identityProof==null?"":formatDateForHTML(identityProof.getIssueDate()))
	                    .build(),
	            FieldDTO.builder()
	                    .name("expiryDate")
	                    .label("Expiry Date")
	                    .type("date")
	                    .required(true)
	                    .value(identityProof==null?"":formatDateForHTML(identityProof.getExpiryDate()))
	                    .build()
	        );
	    }
	    private static String formatDateForHTML(LocalDate date) {
	        if (date == null) {
	            return "";
	        }
	        return date.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
	    }
	    

	    @Transactional
	    public void updateIdentityFields(
	            Long userId,
	            List<UserIdentityUpdateRequest> updateRequests) {

	    	User user=userRepository.findById(userId).orElseGet(null);

	        for (UserIdentityUpdateRequest request : updateRequests) {

	            UserIdentityProof useridentityProof = userIdentityProofRepository
	                    .findByUser_UserIdAndDocTypeId(
	                            userId,request.getTypeId()
	                    )
	                    .orElseGet(() -> UserIdentityProof.builder()
	                            .docTypeId(request.getTypeId())
	                            .user(user)
	                            .build()
	                    );

	            updateIdentityProofFromRequest(useridentityProof, request);
	            
	            log.info("request:::::::::::::::::::::::::::::{}",request.getType());

	            userIdentityProofRepository.save(useridentityProof);
	        }
	    }


	    private void updateIdentityProofFromRequest(UserIdentityProof identityProof, UserIdentityUpdateRequest identityRequest) {
	        for (FieldDTO field : identityRequest.getFields()) {
	            switch (field.getName()) {
	                case "documentNumber":
	                    identityProof.setDocumentNumber(field.getValue());
	                    break;
	                case "issueDate":
	                    if (field.getValue() != null && !field.getValue().trim().isEmpty()) {
	                        try {
	                            LocalDate localDate = LocalDate.parse(field.getValue());
	                           // LocalDate issueDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	                            identityProof.setIssueDate(localDate);
	                        } catch (Exception e) {
	                            throw new RuntimeException("Invalid issue date format for " + identityRequest.getType() + ": " + field.getValue());
	                        }
	                    }
	                    break;
	                case "expiryDate":
	                    if (field.getValue() != null && !field.getValue().trim().isEmpty()) {
	                        try {
	                            LocalDate localDate = LocalDate.parse(field.getValue());
	                           // Date expiryDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	                            identityProof.setExpiryDate(localDate);
	                        } catch (Exception e) {
	                            throw new RuntimeException("Invalid expiry date format for " + identityRequest.getType() + ": " + field.getValue());
	                        }
	                    }
	                    break;
	                default:
	                    // Handle other fields if needed
	                    break;
	            }
	        }

	        // Update the timestamp
	      //  identityProof.setUploadedAt(LocalDateTime.now());
	    }
}
