package com.org.bgv.vendor.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.catalina.security.SecurityUtil;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.candidate.repository.CandidateRepository;
import com.org.bgv.common.EvidenceLevel;
import com.org.bgv.common.EvidenceStatus;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.constants.VerificationStatus;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.DocumentType;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.repository.DocumentTypeRepository;
import com.org.bgv.repository.VerificationCaseCheckRepository;
import com.org.bgv.repository.VerificationCaseRepository;
import com.org.bgv.s3.S3StorageService;
import com.org.bgv.vendor.dto.DocumentEvidenceDTO;
import com.org.bgv.vendor.dto.EvidenceContainerDTO;
import com.org.bgv.vendor.dto.EvidenceDTO;
import com.org.bgv.vendor.dto.EvidenceDocumentTypeDTO;
import com.org.bgv.vendor.dto.EvidenceGroupDTO;
import com.org.bgv.vendor.dto.EvidenceResponseDTO;
import com.org.bgv.vendor.dto.ObjectEvidenceDTO;
import com.org.bgv.vendor.dto.SectionEvidenceDTO;
import com.org.bgv.vendor.dto.VerificationEvidenceResponseDTO;
import com.org.bgv.vendor.entity.CategoryEvidenceType;
import com.org.bgv.vendor.entity.EvidenceType;
import com.org.bgv.vendor.entity.VerificationEvidence;
import com.org.bgv.vendor.repository.CategoryEvidenceTypeRepository;
import com.org.bgv.vendor.repository.VerificationEvidenceRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationEvidenceService {

    private final VerificationCaseRepository caseRepository;
    private final VerificationCaseCheckRepository checkRepository;
    private final CandidateRepository candidateRepository;
    private final DocumentTypeRepository documentTypeRepository;
    private final VerificationEvidenceRepository evidenceRepository;
    private final S3StorageService s3StorageService; // S3, local, etc
    private final CategoryEvidenceTypeRepository categoryEvidenceTypeRepository;

    @Transactional
    public EvidenceResponseDTO uploadEvidence(
            Long caseId,
            Long candidateId,
            Long caseCheckId,
            Long objectId,
            Long docTypeId,
            MultipartFile file,
            String remarks,
            boolean confidential
    ) {

        VerificationCase verificationCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new IllegalArgumentException("Verification case not found"));

        VerificationCaseCheck check = checkRepository.findById(caseCheckId)
                .orElseThrow(() -> new IllegalArgumentException("Verification check not found"));

        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new IllegalArgumentException("Candidate not found"));

        if (!check.getVerificationCase().getCaseId().equals(caseId)) {
            throw new IllegalArgumentException("Check does not belong to case");
        }

        EvidenceLevel level = resolveLevel(objectId, docTypeId);

        DocumentType documentType = null;
        if (docTypeId != null) {
            documentType = documentTypeRepository.findById(docTypeId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid document type"));
        }

        // Upload file
        Pair<String, String> storedFile = s3StorageService.uploadFile(file, "EVIDENCE");
        
        VerificationEvidence evidence = VerificationEvidence.builder()
                .verificationCase(verificationCase)
                .verificationCaseCheck(check)
                .candidate(candidate)
                .category(check.getCategory())
                .objectId(objectId)
                .documentType(documentType)
               // .fileName(file.getFileName())
                .originalFileName(file.getOriginalFilename())
                .fileUrl(storedFile.getFirst())
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .awsDocKey(storedFile.getSecond())
                .evidenceLevel(level)
                .remarks(remarks)
                .uploadedById(SecurityUtils.getCurrentUserId())
               // .uploadedByRole(SecurityUtil.getUserRole())
                .status(EvidenceStatus.UPLOADED)
                .archived(false)
                .build();

        VerificationEvidence saved = evidenceRepository.save(evidence);

        log.info("Evidence uploaded: id={}, level={}", saved.getId(), level);

        return mapToDto(saved);
    }

    private EvidenceLevel resolveLevel(Long objectId, Long docTypeId) {
        if (docTypeId != null) return EvidenceLevel.DOCUMENT;
        if (objectId != null) return EvidenceLevel.OBJECT;
        return EvidenceLevel.SECTION;
    }
    
    private EvidenceResponseDTO mapToDto(VerificationEvidence e) {
        return EvidenceResponseDTO.builder()
                .evidenceId(e.getId())
                .level(e.getEvidenceLevel())
                .fileName(e.getOriginalFileName())
                .fileUrl(e.getFileUrl())
                .status(e.getStatus())
                .objectId(e.getObjectId())
                .documentTypeId(
                    e.getDocumentType() != null ? e.getDocumentType().getDocTypeId() : null
                )
                .docId(e.getDocId())
                .uploadedAt(e.getUploadedAt())
                .build();
    }
    
   

    public VerificationEvidenceResponseDTO buildEvidenceResponse(
            Long caseId,
            Long checkId
    ) {
    	
    	
        VerificationCase verificationCase = caseRepository.findById(caseId)
        .orElseThrow(() -> new IllegalArgumentException("Verification case not found"));

       VerificationCaseCheck check = checkRepository.findById(checkId)
        .orElseThrow(() -> new IllegalArgumentException("Verification check not found"));

        List<VerificationEvidence> evidences =
        		evidenceRepository.findAllByCheckId(checkId);

        // SECTION
        SectionEvidenceDTO section = SectionEvidenceDTO.builder()
                .level(EvidenceLevel.SECTION)
                .evidences(mapEvidence(
                        evidences.stream()
                                .filter(e -> e.getEvidenceLevel() == EvidenceLevel.SECTION)
                                .toList()
                ))
                .build();

        // OBJECT GROUPING
        Map<Long, List<VerificationEvidence>> objectMap =
                evidences.stream()
                        .filter(e -> e.getObjectId() != null)
                        .collect(Collectors.groupingBy(VerificationEvidence::getObjectId));

        List<ObjectEvidenceDTO> objects = new ArrayList();

        for (var entry : objectMap.entrySet()) {
            Long objectId = entry.getKey();
            List<VerificationEvidence> objectEvidences = entry.getValue();

            // OBJECT LEVEL
            EvidenceGroupDTO objectEvidence = EvidenceGroupDTO.builder()
                    .level(EvidenceLevel.OBJECT)
                    .objectId(objectId)
                    .evidences(mapEvidence(
                            objectEvidences.stream()
                                    .filter(e -> e.getEvidenceLevel() == EvidenceLevel.OBJECT)
                                    .toList()
                    ))
                    .build();

            // DOCUMENT GROUPING
            Map<Object, List<VerificationEvidence>> docMap =
                    objectEvidences.stream()
                            .filter(e -> e.getDocumentType() != null)
                            .collect(Collectors.groupingBy(e -> e.getDocumentType().getDocTypeId()));

            List<DocumentEvidenceDTO> documents = new ArrayList<>();

            for (var docEntry : docMap.entrySet()) {
                VerificationEvidence sample = docEntry.getValue().get(0);

                documents.add(
                        DocumentEvidenceDTO.builder()
                                .documentType(
                                        mapDocumentType(sample.getDocumentType())
                                )
                                .documentEvidence(
                                        EvidenceGroupDTO.builder()
                                                .level(EvidenceLevel.DOCUMENT)
                                                .objectId(objectId)
                                                .documentTypeId(
                                                        sample.getDocumentType().getDocTypeId()
                                                )
                                                .evidences(mapEvidence(docEntry.getValue()))
                                                .build()
                                )
                                .build()
                );
            }

            objects.add(
                    ObjectEvidenceDTO.builder()
                            .objectId(objectId)
                            .objectType(check.getCategory().getLabel())
                          //  .displayName(resolveDisplayName(objectId, check))
                            .objectEvidence(objectEvidence)
                            .documents(documents)
                            .build()
            );
        }

        return VerificationEvidenceResponseDTO.builder()
                .caseId(verificationCase.getCaseId())
                .caseRef(verificationCase.getCaseNumber())
                .checkId(check.getCaseCheckId())
                .checkRef(check.getCheckRef())
                .checkType(check.getCategory().getLabel())
                //.checkName(check.getCheckType().getDisplayName())
                .evidence(
                        EvidenceContainerDTO.builder()
                                .section(section)
                                .objects(objects)
                                .build()
                )
                .build();
    }
    
    private List<EvidenceDTO> mapEvidence(List<VerificationEvidence> list) {
        return list.stream()
                .map(e -> EvidenceDTO.builder()
                        .evidenceId(e.getId())
                        .fileName(e.getFileName())
                        .originalFileName(e.getOriginalFileName())
                        .status(e.getStatus())
                        .remarks(e.getRemarks())
                        .uploadedAt(e.getUploadedAt())
                     //   .uploadedBy(
                             //   UploadedByDTO.builder()
                              //          .id(e.getUploadedById())
                                       // .role(e.getUploadedByRole())
                                       // .build()
                        
                        .build())
                .toList();
    }
    
    private EvidenceDocumentTypeDTO mapDocumentType(DocumentType d) {
        return EvidenceDocumentTypeDTO.builder()
                .id(d.getDocTypeId())
                .code(d.getCode())
                .name(d.getName())
                .build();
    }
    /*
    private String resolveDisplayName(Long objectId, VerificationCaseCheck check) {
        return switch (check.getCheckType()) {
            case WORK -> "Company #" + objectId;
            case EDUCATION -> "Degree #" + objectId;
            case IDENTITY -> "Identity Record";
        };
    }
*/
    
 // EVIDENCE
	
 	@Transactional
 	public void uploadEvidencewithEvidenceType(
 	        Long candidateId,
 	        Long caseId,
 	        Long caseCheckId,
 	        Long docTypeId,
 	        Long objectId,
 	        Long evidenceTypeId,
 	        String description,
 	        MultipartFile file
 	) {

 	    /* ===============================
 	       1. Load Verification Context
 	       =============================== */

 	    VerificationCaseCheck check =
 	    		checkRepository.findById(caseCheckId)
 	                    .orElseThrow(() -> new IllegalArgumentException("Invalid caseCheckId"));

 	    CheckCategory category = check.getCategory();

 	    /* ===============================
 	       2. Validate EvidenceType for Category
 	       =============================== */

 	    CategoryEvidenceType categoryEvidenceType =
 	            categoryEvidenceTypeRepository
 	                    .findByCategoryCategoryIdAndEvidenceTypeIdAndActiveTrue(
 	                            category.getCategoryId(),
 	                            evidenceTypeId
 	                    )
 	                    .orElseThrow(() ->
 	                            new IllegalArgumentException(
 	                                    "Evidence type not allowed for this category"
 	                            )
 	                    );

 	    EvidenceType evidenceType = categoryEvidenceType.getEvidenceType();

 	    /* ===============================
 	       3. Validate rules
 	       =============================== */

 	    if (Boolean.TRUE.equals(evidenceType.getRequiresFile()) && file == null) {
 	        throw new IllegalArgumentException("File is mandatory for this evidence type");
 	    }

 	    if (Boolean.TRUE.equals(evidenceType.getRequiresRemarks())
 	            && (description == null || description.isBlank())) {
 	        throw new IllegalArgumentException("Remarks are mandatory for this evidence type");
 	    }

 	    /* ===============================
 	       4. Determine Evidence Level
 	       =============================== */

 	    EvidenceLevel evidenceLevel =
 	            (docTypeId == null)
 	                    ? EvidenceLevel.SECTION
 	                    : EvidenceLevel.DOCUMENT;

 	    /* ===============================
 	       5. Upload file to S3
 	       =============================== */

 	    Pair<String, String> upload =
 	            s3StorageService.uploadFile(
 	                    file,
 	                    "evidence/" + caseId
 	            );

 	    /* ===============================
 	       6. Build VerificationEvidence
 	       =============================== */

 	    VerificationEvidence evidence = VerificationEvidence.builder()
 	            .verificationCase(
 	            		caseRepository.getReferenceById(caseId)
 	            )
 	            .verificationCaseCheck(check)
 	            .candidate(
 	                    candidateRepository.getReferenceById(candidateId)
 	            )
 	            .category(category)

 	            .documentType(
 	                    docTypeId != null
 	                            ? documentTypeRepository.getReferenceById(docTypeId)
 	                            : null
 	            )
 	            .objectId(
 	                    evidenceLevel == EvidenceLevel.DOCUMENT ? objectId : null
 	            )

 	            .fileName(file.getOriginalFilename())
 	            .originalFileName(file.getOriginalFilename())
 	            .fileUrl(upload.getFirst())
 	            .awsDocKey(upload.getSecond())
 	            .fileSize(file.getSize())
 	            .fileType(file.getContentType())

 	            .evidenceLevel(evidenceLevel)
 	            .remarks(description)

 	            .uploadedById(SecurityUtils.getCurrentUserId())
 	            .uploadedByRole("VENDOR")
 	            .status(EvidenceStatus.UPLOADED)
 	            .build();

 	    /* ===============================
 	       7. Save
 	       =============================== */

 	   evidenceRepository.save(evidence);
 	}
 	


}
