package com.org.bgv.service;

import com.org.bgv.dto.DocumentResponse;
import com.org.bgv.dto.DocumentStats;
import com.org.bgv.dto.DocumentUploadRequest;
import com.org.bgv.dto.FieldDTO;
import com.org.bgv.dto.IdentityProofDTO;
import com.org.bgv.dto.IdentityProofResponse;
import com.org.bgv.dto.IdentitySectionRequest;
import com.org.bgv.dto.UploadRuleDTO;
import com.org.bgv.entity.DocumentCategory;
import com.org.bgv.entity.IdentityDocuments;
import com.org.bgv.entity.IdentityProof;
import com.org.bgv.entity.Profile;
import com.org.bgv.repository.DocumentCategoryRepository;
import com.org.bgv.repository.IdentityDocumentsRepository;
import com.org.bgv.repository.IdentityProofRepository;
import com.org.bgv.repository.ProfileRepository;
import com.org.bgv.s3.S3StorageService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
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
    private final DocumentCategoryRepository documentCategoryRepository;
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
        long verified = docs.stream().filter(d -> "VERIFIED".equalsIgnoreCase(d.getStatus())).count();
        long pending = docs.stream().filter(d -> "PENDING".equalsIgnoreCase(d.getStatus())).count();
        long rejected = docs.stream().filter(d -> "REJECTED".equalsIgnoreCase(d.getStatus())).count();

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
    
    
    
    public IdentitySectionRequest createIdentitySectionResponse(Long candidateId) {
    	
    	String section = "IDENTITY_PROOF";
    	
    	 Optional<DocumentCategory> documentCategoryOpt = documentCategoryRepository.findByNameIgnoreCase(section);
         
         if (documentCategoryOpt.isEmpty()) {
             throw new RuntimeException("Section not found: " + section);
         }
    	
    	
    	
        return IdentitySectionRequest.builder()
                .section("Identity")
                .label("Identity")
                .documents(createIdentityDocuments(candidateId))
                .build();
    }
    
    private static List<DocumentUploadRequest> createIdentityDocuments(Long candidateId) {
        return List.of(
            createAadharDocument(candidateId),
            createPanCardDocument(),
            createPassportDocument()
        );
    }
    
    private static DocumentUploadRequest createAadharDocument(Long candidateId) {
        return DocumentUploadRequest.builder()
                .type("AADHAR")
                .label("Aadhar Card")
                .fields(createAadharFields())
                .upload(UploadRuleDTO.builder()
                        .multiple(true)
                        .required(true)
                        .build())
                .savedDocuments(List.of()) // Empty for new uploads
                .filesToAdd(null) // Will be set during file upload
                .filesToDelete(List.of()) // No files to delete initially
                .build();
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
    
    private static DocumentUploadRequest createPanCardDocument() {
        return DocumentUploadRequest.builder()
                .type("PAN")
                .label("PAN Card")
                .fields(createPanFields())
                .upload(UploadRuleDTO.builder()
                        .multiple(false)
                        .required(true)
                        .build())
                .savedDocuments(List.of())
                .filesToAdd(null)
                .filesToDelete(List.of())
                .build();
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
    
    private static DocumentUploadRequest createPassportDocument() {
        return DocumentUploadRequest.builder()
                .type("PASSPORT")
                .label("Passport")
                .fields(createPassportFields())
                .upload(UploadRuleDTO.builder()
                        .multiple(false)
                        .required(false)
                        .build())
                .savedDocuments(List.of())
                .filesToAdd(null)
                .filesToDelete(List.of())
                .build();
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
