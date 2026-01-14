package com.org.bgv.service;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.candidate.entity.IdentityProof;
import com.org.bgv.candidate.repository.CandidateRepository;
import com.org.bgv.candidate.repository.IdentityProofRepository;
import com.org.bgv.common.DocumentStatus;
import com.org.bgv.dto.DocumentResponse;
import com.org.bgv.dto.DocumentStats;
import com.org.bgv.dto.DocumentUploadRequest;
import com.org.bgv.dto.FieldDTO;
import com.org.bgv.dto.IdentityProofDTO;
import com.org.bgv.dto.IdentityProofResponse;
import com.org.bgv.dto.IdentitySectionRequest;
import com.org.bgv.dto.UploadRuleDTO;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.DocumentType;
import com.org.bgv.entity.IdentityDocuments;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.repository.DocumentTypeRepository;
import com.org.bgv.repository.IdentityDocumentsRepository;
import com.org.bgv.repository.ProfileRepository;
import com.org.bgv.repository.VerificationCaseCheckRepository;
import com.org.bgv.repository.VerificationCaseRepository;
import com.org.bgv.s3.S3StorageService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class IdentityProofService {

    private final IdentityProofRepository identityProofRepository;
    private final IdentityDocumentsRepository identityDocumentsRepository;
    private final ProfileRepository profileRepository;
    private final S3StorageService s3StorageService;
    private final CheckCategoryRepository checkCategoryRepository;
    private final CandidateRepository candidateRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final VerificationCaseRepository verificationCaseRepository;
    private final VerificationCaseCheckRepository verificationCaseCheckRepository;
    /**
     * Fetch all identity proofs with their related documents for a given profile.
     */
    @Transactional(readOnly = true)
    public IdentityProofResponse getIdentityProofsWithDocuments(Long profileId) {
        log.info("Fetching IdentityProofs for profileId={}", profileId);

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found with ID: " + profileId));

        List<IdentityProof> proofs = identityProofRepository.findByProfile(profile);
        if (proofs.isEmpty()) {
            return IdentityProofResponse.builder()
                    .proofs(Collections.emptyList())
                    .summary(new DocumentStats(0, 0, 0, 0))
                    .build();
        }

        List<Long> proofIds = proofs.stream().map(IdentityProof::getId).collect(Collectors.toList());
        List<IdentityDocuments> documents =
                identityDocumentsRepository.findByProfile_ProfileIdAndObjectIdIn(profileId, proofIds);

        List<IdentityProofDTO> proofDetails = proofs.stream()
                .map(proof -> convertToDTO(proof, documents))
                .collect(Collectors.toList());

     //   DocumentStats summary = calculateSummary(proofDetails);

        return IdentityProofResponse.builder()
                .proofs(proofDetails)
            //    .summary(summary)
                .build();
    }

    private IdentityProofDTO convertToDTO(IdentityProof proof, List<IdentityDocuments> documents) {
        List<DocumentResponse> proofDocs = documents.stream()
                .filter(doc -> doc.getObjectId() != null && doc.getObjectId().equals(proof.getId()))
                .map(this::convertToDocumentResponse)
                .collect(Collectors.toList());

      //  DocumentStats stats = calculateStats(proofDocs);

        return IdentityProofDTO.builder()
                .id(proof.getId())
               
                .documentNumber(proof.getDocumentNumber())
                .status(proof.getStatus())
                .uploadedAt(proof.getUploadedAt())
                .updatedBy(proof.getUpdatedBy())
                .documents(proofDocs)
               // .documentStats(stats)
                .build();
    }

    private DocumentResponse convertToDocumentResponse(IdentityDocuments doc) {
        return DocumentResponse.builder()
                .doc_id(doc.getDocId())
                .category_id(doc.getCategory() != null ? doc.getCategory().getCategoryId() : null)
                .category_name(doc.getCategory() != null ? doc.getCategory().getName() : null)
                .doc_type_id(doc.getDocTypeId() != null ? doc.getDocTypeId().getDocTypeId() : null)
                .document_type_name(doc.getDocTypeId() != null ? doc.getDocTypeId().getName() : null)
                .file_url(doc.getFileUrl())
                .file_size(doc.getFileSize())
                .file_name(extractFileName(doc.getFileUrl()))
                .file_type(extractFileType(doc.getFileUrl()))
                .status(doc.getStatus())
                .uploadedAt(doc.getUploadedAt())
                .verifiedAt(doc.getVerifiedAt())
                .comments(doc.getComments())
                .awsDocKey(doc.getAwsDocKey())
                .build();
    }

    private DocumentStats calculateStats(List<DocumentResponse> docs) {

        long total = docs.size();

        long verified = docs.stream()
            .filter(d -> d.getStatus() == DocumentStatus.VERIFIED)
            .count();

        long pending = docs.stream()
            .filter(d -> d.getStatus() == DocumentStatus.PENDING)
            .count();

        long rejected = docs.stream()
            .filter(d -> d.getStatus() == DocumentStatus.REJECTED)
            .count();

        return new DocumentStats(total, verified, pending, rejected);
    }

/*
    private DocumentStats calculateSummary(List<IdentityProofDTO> proofDetails) {
        long total = proofDetails.stream().mapToLong(p -> p.getDocumentStats().getTotalDocuments()).sum();
        long verified = proofDetails.stream().mapToLong(p -> p.getDocumentStats().getVerified()).sum();
        long pending = proofDetails.stream().mapToLong(p -> p.getDocumentStats().getPending()).sum();
        long rejected = proofDetails.stream().mapToLong(p -> p.getDocumentStats().getRejected()).sum();

        return new DocumentStats(total, verified, pending, rejected);
    }
*/
    private String extractFileName(String fileUrl) {
        if (fileUrl == null) return null;
        return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
    }

    private String extractFileType(String fileUrl) {
        if (fileUrl == null) return "UNKNOWN";
        int dotIndex = fileUrl.lastIndexOf(".");
        return dotIndex > 0 ? fileUrl.substring(dotIndex + 1).toUpperCase() : "UNKNOWN";
    }
    public void deleteIdentityByprofileId(Long profileId) {
    	// First, handle AWS file deletions
        List<IdentityDocuments> allDocuments = identityDocumentsRepository.findByProfile_ProfileId(profileId);
        
        for (IdentityDocuments document : allDocuments) {
            if (document.getAwsDocKey() != null) {
                s3StorageService.deleteFile(document.getAwsDocKey());
            }
        }
    	identityDocumentsRepository.deleteByProfile_ProfileId(profileId);
    	identityProofRepository.deleteByProfile_ProfileId(profileId);
    	
    }
    
    
    
    public IdentitySectionRequest createIdentitySectionResponse(Long candidateId, Long caseId) {

        final String CATEGORY_NAME = "Identity";

        // Fetch category
        CheckCategory category = checkCategoryRepository
                .findByNameIgnoreCase(CATEGORY_NAME)
                .orElseThrow(() -> new RuntimeException("Category not found: " + CATEGORY_NAME));

        // Fetch case
        VerificationCase verificationCase = verificationCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Verification case not found"));

        // Fetch case check
        VerificationCaseCheck identityCheck = verificationCaseCheckRepository
                .findByVerificationCaseAndCategory(verificationCase, category)
                .orElseThrow(() -> new RuntimeException("Identity check not created"));

        return IdentitySectionRequest.builder()
                .section(category.getName())
                .label(category.getName())
                .checkId(identityCheck.getCaseCheckId())   // ✅ FIXED
                .checkRef(identityCheck.getCheckRef())     // ✅ FIXED
                .documents(createIdentityDocuments(candidateId, verificationCase, identityCheck))
                .build();
    }
    
    private List<DocumentUploadRequest> createIdentityDocuments(
            Long candidateId,
            VerificationCase verificationCase,
            VerificationCaseCheck identityCheck) {

        CheckCategory category = identityCheck.getCategory();

        List<DocumentType> documentTypes =
                documentTypeRepository.findByCategoryCategoryId(category.getCategoryId());

        if (documentTypes.isEmpty()) {
            return Collections.emptyList();
        }

        List<DocumentUploadRequest> documentList = new ArrayList<>();

        for (DocumentType documentType : documentTypes) {

            Optional<IdentityProof> identityProofOpt =
                    identityProofRepository
                            .findByCandidate_CandidateIdAndDocTypeIdAndVerificationCaseAndVerificationCaseCheckAndCompanyId(
                                    candidateId,
                                    documentType.getDocTypeId(),
                                    verificationCase,
                                    identityCheck,
                                    verificationCase.getCompanyId()
                            );

            DocumentUploadRequest request =
                    createDocumentByType(documentType, identityProofOpt.orElse(null));

            if (request != null) {
                documentList.add(request);
            }
        }

        return documentList;
    }

    
    private DocumentUploadRequest createDocumentByType(DocumentType documentType,IdentityProof identityProof) {
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
    
    private static DocumentUploadRequest createAadharDocument(DocumentType documentType, IdentityProof identityProof) {
        return DocumentUploadRequest.builder()
                .id(identityProof!=null?identityProof.getId():null)
        		.type("AADHAR")
                .label("Aadhar Card")
                .typeId(documentType.getDocTypeId())
                .fields(createAadharFields(identityProof))
              
                .build();
    }
    
    private static List<FieldDTO> createAadharFields(IdentityProof identityProof) {
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
    
    private static DocumentUploadRequest createPanCardDocument(DocumentType documentType, IdentityProof identityProof) {
        return DocumentUploadRequest.builder()
                .type("PAN")
                .label("PAN Card")
                .typeId(documentType.getDocTypeId())
                .fields(createPanFields(identityProof))
               
                .build();
    }
    
    private static List<FieldDTO> createPanFields(IdentityProof identityProof) {
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
    
    private static DocumentUploadRequest createPassportDocument(DocumentType documentType, IdentityProof identityProof) {
        return DocumentUploadRequest.builder()
                .type("PASSPORT")
                .label("Passport")
                .typeId(documentType.getDocTypeId())
                .fields(createPassportFields(identityProof))
                
                .build();
    }
    
    private static List<FieldDTO> createPassportFields(IdentityProof identityProof) {
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
            Long candidateId,
            Long caseId,
            Long checkId,
            List<DocumentUploadRequest> updateRequests) {

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found: " + candidateId));

        VerificationCase verificationCase = verificationCaseRepository.findById(caseId)
                .orElseThrow(() -> new RuntimeException("Verification case not found: " + caseId));

        VerificationCaseCheck caseCheck = verificationCaseCheckRepository.findById(checkId)
                .orElseThrow(() -> new RuntimeException("Verification case check not found: " + checkId));

        Long companyId = verificationCase.getCompanyId();

        for (DocumentUploadRequest request : updateRequests) {

            IdentityProof identityProof = identityProofRepository
                    .findByCandidate_CandidateIdAndDocTypeIdAndVerificationCaseAndVerificationCaseCheckAndCompanyId(
                            candidateId,
                            request.getTypeId(),
                            verificationCase,
                            caseCheck,
                            companyId
                    )
                    .orElseGet(() -> IdentityProof.builder()
                            .candidate(candidate)
                            .verificationCase(verificationCase)
                            .verificationCaseCheck(caseCheck)
                            .companyId(companyId)
                            .docTypeId(request.getTypeId())
                            .status(DocumentStatus.PENDING.name())
                            .verificationStatus("pending")
                            .verified(false)
                            .build()
                    );

            updateIdentityProofFromRequest(identityProof, request);

            identityProofRepository.save(identityProof);
        }
    }

    
    private void updateIdentityProofFromRequest(IdentityProof identityProof, DocumentUploadRequest documentRequest) {
        for (FieldDTO field : documentRequest.getFields()) {
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
                            throw new RuntimeException("Invalid issue date format for " + documentRequest.getType() + ": " + field.getValue());
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
                            throw new RuntimeException("Invalid expiry date format for " + documentRequest.getType() + ": " + field.getValue());
                        }
                    }
                    break;
                default:
                    // Handle other fields if needed
                    break;
            }
        }

        // Update the timestamp
        identityProof.setUploadedAt(LocalDateTime.now());
    }
 // Get all identity proofs for a candidate
    public List<IdentityProof> getIdentityProofsByCandidate(Long candidateId) {
        return identityProofRepository.findByCandidate_CandidateId(candidateId);
    }

    // verification
    
    
    @Cacheable(value = "identity", key = "#candidateId")
    public List<IdentityProofDTO> getIdentityInfo(Long candidateId) {

        log.info("Fetching identity information for candidate: {}", candidateId);

        List<IdentityProof> identities =
                identityProofRepository.findByCandidate_CandidateId(candidateId);

        if (identities.isEmpty()) {
            throw new RuntimeException("No identity proofs found for candidate " + candidateId);
        }

        return identities.stream()
                .map(this::convertToDTO)
                .toList();
    }
    private IdentityProofDTO convertToDTO(IdentityProof identity) {
    	IdentityProofDTO dto = new IdentityProofDTO();
        dto.setId(identity.getId());
        dto.setCandidateId(identity.getCandidate().getCandidateId());
        dto.setDocumentTypeId(identity.getDocTypeId());
        dto.setDocumentNumber(identity.getDocumentNumber());
        dto.setIssueDate(identity.getIssueDate());
        dto.setExpiryDate(identity.getExpiryDate());
      //  dto.setIssuingAuthority(identity.getIssuingAuthority());
      //  dto.setIssuingCountry(identity.getIssuingCountry());
      //  dto.setPlaceOfIssue(identity.getPlaceOfIssue());
        dto.setVerified(identity.getVerified()==null?Boolean.FALSE:identity.getVerified());
        dto.setVerificationStatus(identity.getVerificationStatus());
        dto.setVerifiedBy(identity.getVerifiedBy());
        dto.setVerifiedAt(identity.getVerifiedAt());
        
        // Convert field values
       // Map<String, String> fieldValues = getFieldValuesMap(identity);
      //  dto.setFieldValues(fieldValues);
        
        // Calculate derived fields
        dto.setDocumentExpired(isDocumentExpired(identity));
        dto.setDaysUntilExpiry(getDaysUntilExpiry(identity));
        
        return dto;
    }

    private boolean isDocumentExpired(IdentityProof identity) {
        if (identity.getExpiryDate() == null) {
            return false;
        }
        return identity.getExpiryDate().isBefore(java.time.LocalDate.now());
    }
    
    private Long getDaysUntilExpiry(IdentityProof identity) {
        if (identity.getExpiryDate() == null) {
            return null;
        }
        
        java.time.LocalDate today = java.time.LocalDate.now();
        if (today.isAfter(identity.getExpiryDate())) {
            return 0L; // Already expired
        }
        
        return java.time.temporal.ChronoUnit.DAYS.between(today, identity.getExpiryDate());
    }
    
   
    
}
