package com.org.bgv.vendor.service;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.candidate.entity.EducationHistory;
import com.org.bgv.candidate.entity.IdentityProof;
import com.org.bgv.candidate.entity.WorkExperience;
import com.org.bgv.candidate.repository.CandidateRepository;
import com.org.bgv.candidate.repository.EducationHistoryRepository;
import com.org.bgv.candidate.repository.IdentityProofRepository;
import com.org.bgv.candidate.repository.WorkExperienceRepository;
import com.org.bgv.commom.dto.OptionDTO;
import com.org.bgv.common.CheckObjectType;
import com.org.bgv.common.DocumentEntityType;
import com.org.bgv.common.DocumentStatus;
import com.org.bgv.common.DocumentTypeInfo;
import com.org.bgv.common.Option;
import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.entity.*;
import com.org.bgv.enums.ComparisonStatus;
import com.org.bgv.enums.VendorNoteType;
import com.org.bgv.repository.*;
import com.org.bgv.vendor.action.dto.ActionDTO;
import com.org.bgv.vendor.action.dto.VendorActionCatalog;
import com.org.bgv.vendor.builder.FieldsUtil;
import com.org.bgv.vendor.builder.ObjectFieldBuilderRegistry;
import com.org.bgv.vendor.dto.ActionType;
import com.org.bgv.vendor.dto.CandidateInfoDTO;
import com.org.bgv.vendor.dto.DeclaredEducationInfoDTO;
import com.org.bgv.vendor.dto.DeclaredEmploymentInfoDTO;
import com.org.bgv.vendor.dto.DeclaredIdentityInfoDTO;
import com.org.bgv.vendor.dto.DocumentTypeVerificationDTO;
import com.org.bgv.vendor.dto.EducationCheckDTO;
import com.org.bgv.vendor.dto.EducationContextDTO;
import com.org.bgv.vendor.dto.EmployerInfoDTO;
import com.org.bgv.vendor.dto.EmploymentCheckDTO;
import com.org.bgv.vendor.dto.EmploymentContextDTO;
import com.org.bgv.vendor.dto.EvidenceDTO;
import com.org.bgv.vendor.dto.EvidenceTypeDTO;
import com.org.bgv.vendor.dto.IdentityCheckDTO;
import com.org.bgv.vendor.dto.IdentityContextDTO;
import com.org.bgv.vendor.dto.ObjectComparisonFieldDTO;
import com.org.bgv.vendor.dto.ObjectDTO;
import com.org.bgv.vendor.dto.ObjectFieldDTO;
import com.org.bgv.vendor.dto.RequirementDTO;
import com.org.bgv.vendor.dto.SlaInfoDTO;
import com.org.bgv.vendor.dto.TimelineEventDTO;
import com.org.bgv.vendor.dto.VendorNoteDTO;
import com.org.bgv.vendor.dto.VendorVerificationCheckDTO;
import com.org.bgv.vendor.dto.VerificationCheckResponseDTO;
import com.org.bgv.vendor.dto.VerificationDocumentDTO;
import com.org.bgv.vendor.dto.VerificationFileDTO;
import com.org.bgv.vendor.dto.VerificationHistoryDTO;
import com.org.bgv.vendor.entity.CategoryEvidenceType;
import com.org.bgv.vendor.entity.EvidenceType;
import com.org.bgv.vendor.entity.VendorNote;
import com.org.bgv.vendor.entity.VerificationCheckHistory;
import com.org.bgv.vendor.entity.VerificationObject;
//import com.org.bgv.vendor.entity.VerificationEvidence;
import com.org.bgv.vendor.entity.VerificationTimeline;
import com.org.bgv.vendor.repository.CategoryEvidenceTypeRepository;
import com.org.bgv.vendor.repository.EvidenceTypeRepository;
import com.org.bgv.vendor.repository.VendorNoteRepository;
import com.org.bgv.vendor.repository.VerificationCheckHistoryRepository;
import com.org.bgv.vendor.repository.VerificationFieldComparisonRepository;
import com.org.bgv.vendor.repository.VerificationObjectRepository;
//import com.org.bgv.vendor.repository.VerificationEvidenceRepository;
import com.org.bgv.vendor.repository.VerificationTimelineRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCheckService {

	private final VerificationCaseCheckRepository verificationCaseCheckRepository;
	private final VerificationCaseRepository verificationCaseRepository;
	private final CandidateRepository candidateRepository;
	private final CompanyRepository companyRepository;
	private final VerificationCaseDocumentRepository verificationCaseDocumentRepository;
	private final VendorNoteRepository vendorNoteRepository;
	// private final VerificationEvidenceRepository evidenceRepository;
	private final VerificationTimelineRepository timelineRepository;
	private final VerificationCheckHistoryRepository historyRepository;
	private final VerificationCaseDocumentLinkRepository verificationCaseDocumentLinkRepository;
	private final EvidenceTypeRepository evidenceTypeRepository;
	private final DocumentTypeRepository documentTypeRepository;
	private final CategoryEvidenceTypeRepository categoryEvidenceTypeRepository;
	private final IdentityProofRepository identityProofRepository;
	private final DocumentRepository documentRepository;
	private final EducationHistoryRepository educationHistoryRepository;
	private final CheckCategoryRepository checkCategoryRepository;
	private final WorkExperienceRepository workExperienceRepository;
	private final ObjectFieldBuilderRegistry fieldBuilderRegistry;
	private final VerificationCaseSelectionRepository verificationCaseSelectionRepository;
	private final VerificationFieldComparisonRepository verificationFieldComparisonRepository;
	private final VerificationObjectRepository verificationObjectRepository;


	@Transactional(readOnly = true)
	public VerificationCheckResponseDTO getVerificationCheck(Long checkId, Long vendorId) {

		log.info("Fetching verification check {} for vendor {}", checkId, vendorId);

		VerificationCaseCheck check = verificationCaseCheckRepository.findByVendorIdAndCaseCheckId(vendorId, checkId);
		// .orElseThrow(() -> new RuntimeException("Verification check not found"));

		// 1️⃣ Vendor authorization
		if (!Objects.equals(check.getVendorId(), vendorId)) {
			throw new RuntimeException("Vendor not authorized to access this check");
		}

		VerificationCase verificationCase = check.getVerificationCase();

		Candidate candidate = candidateRepository.findById(verificationCase.getCandidateId())
				.orElseThrow(() -> new RuntimeException("Candidate not found"));

		// 2️⃣ Build base response
		VerificationCheckResponseDTO response = buildVerificationCheckResponse(check, verificationCase, candidate);

		CheckCategory category = checkCategoryRepository.findByCategoryId(check.getCategory().getCategoryId());

		// 3️⃣ Objects (SECTION-WISE DATA)
		response.setObjects(buildObjects(check, category));

		// 4️⃣ Static / unchanged lists
		response.setEvidenceTypeList(getAllowedEvidenceTypes(check.getCategory().getCategoryId()));

		response.setDocumentTypeInfos(getDocumentTypesForCategory(check));

		return response;
	}

	private VerificationCheckResponseDTO buildVerificationCheckResponse(VerificationCaseCheck check,
			VerificationCase verificationCase, Candidate candidate) {

		return VerificationCheckResponseDTO.builder()
				.caseId(String.valueOf(verificationCase.getCaseId()))
				.caseRef(getCaseReference(verificationCase))
				.checkId(String.valueOf(check.getCaseCheckId()))
				.checkRef(check.getCheckRef())
				//.checkType(check.getCategory().getName())
				.checkType(CheckCategoryEnum.fromName(check.getCategory().getName()))
				.checkName(check.getCategory().getName())
				.status(check.getStatus().name())
				.candidate(mapCandidateInfo(candidate))
				.sendNotification(check.getStatus().name().equalsIgnoreCase(CaseCheckStatus.ACTION_REQUIRED.name()))
				// .audit(buildAudit(check))
				 .actions(resolveCheckActions(check.getStatus())) 
				 
				.build();
	}

	private List<ObjectDTO> buildObjects(VerificationCaseCheck check, CheckCategory category) {

		List<ObjectDTO> objects = new ArrayList<>();

		switch (category.getName()) {

		case "Identity" -> {
			objects.addAll(buildIdentityObjects(check));
		}

		case "Education" -> {
			objects.addAll(buildEducationObjects(check));
		}

		case "Work Experience" -> {
			objects.addAll(buildWorkExperienceObjects(check));
		}

		default -> {
			log.warn("Unsupported category '{}' for caseCheckId={}", category.getName(), check.getCaseCheckId());
		}
		}
		return objects;

	}

	private List<ObjectDTO> buildIdentityObjects(VerificationCaseCheck check) {

	    List<IdentityProof> identities = identityProofRepository
	            .findByVerificationCaseCheckCaseCheckId(check.getCaseCheckId());
	    
	    List<VerificationObject> objects =
	            verificationObjectRepository
	                    .findByVerificationCheckAndObjectType(
	                            check,
	                            CheckCategoryEnum.IDENTITY);
	    
	    return objects.stream()
	            .map(object -> {
	            	List<ObjectComparisonFieldDTO> fields = buildComparisonFields(object);
	            	 List<DocumentTypeVerificationDTO> documentTypes = buildDocumentTypes(object.getSourceId(), check,null);
	            	 
	            	 // Compute object status from document types
		                DocumentStatus objectStatus = resolveObjectStatus(documentTypes);
		                
		                return ObjectDTO.builder()
		                        .objectId(object.getSourceId())
		                        .objectType(CheckCategoryEnum.IDENTITY.getName())
		                        .displayName(object.getObjectName())
		                       // .data(buildIdentityData(identity))
		                        .status(objectStatus.name()) // store as string if DTO expects string
		                        .documentTypes(documentTypes)
		                        .evidence(Collections.emptyList())
		                        .actions(VendorActionCatalog.objectActions()) // optionally pass objectStatus to restrict actions
		                        .fields(fields)
		                        .build();
	            	 
	            })
	            .toList();
	    
	    /*

	    return identities.stream()
	            .map(identity -> {
	            	
	            	 List<ObjectFieldDTO> fields =
	                         fieldBuilderRegistry.resolveFields(
	                                 CheckObjectType.IDENTITY,
	                                 identity
	                         );
	                // Compute document types once
	                List<DocumentTypeVerificationDTO> documentTypes = buildDocumentTypes(identity.getId(), check,fields);

	                // Compute object status from document types
	                DocumentStatus objectStatus = resolveObjectStatus(documentTypes);

	                return ObjectDTO.builder()
	                        .objectId(identity.getId())
	                        .objectType("IDENTITY")
	                        .displayName(resolveIdentityName(identity))
	                       // .data(buildIdentityData(identity))
	                        .status(objectStatus.name()) // store as string if DTO expects string
	                        .documentTypes(documentTypes)
	                        .evidence(Collections.emptyList())
	                        .actions(VendorActionCatalog.objectActions()) // optionally pass objectStatus to restrict actions
	                        .build();
	            })
	            .toList();
	            
	            */
	}


	private String resolveIdentityName(IdentityProof identityProof) {

		DocumentType documentType = documentTypeRepository.findById(identityProof.getDocTypeId())
				.orElseThrow(() -> new RuntimeException("Not found"));

		return documentType.getLabel();

	}
	
	

	
	
	private List<ObjectDTO> buildEducationObjects(VerificationCaseCheck check) {

	    List<EducationHistory> educations = educationHistoryRepository
	            .findByVerificationCaseCheck_CaseCheckId(check.getCaseCheckId());
	    
	    List<VerificationObject> objects =
	            verificationObjectRepository
	                    .findByVerificationCheckAndObjectType(
	                            check,
	                            CheckCategoryEnum.EDUCATION);
	    
	    
	    return objects.stream()
	            .map(object -> {
	            	 List<ObjectComparisonFieldDTO> fields = buildComparisonFields(object);
	            	 
	            	 List<DocumentTypeVerificationDTO> documentTypes = buildDocumentTypes(object.getSourceId(), check, null);
	            	 DocumentStatus objectStatus =
		                        resolveObjectStatus(documentTypes);
	            	 
	            	 return ObjectDTO.builder()
		                        .objectId(object.getSourceId())
		                        .objectType(CheckObjectType.EDUCATION.name())
		                        .displayName(object.getObjectName())
		                        // .data(buildEducationData(education)) // not needed anymore
		                        .status(objectStatus.name())
		                        .documentTypes(documentTypes)
		                        .evidence(Collections.emptyList())
		                        .actions(VendorActionCatalog.objectActions())
		                        .fields(fields)
		                        .fieldSatusOptions(getComparisonStatuses())
		                        .build();
	            	
	            }).toList();
/*
	    return educations.stream()
	            .map(education -> {

	                List<ObjectFieldDTO> fields =
	                        fieldBuilderRegistry.resolveFields(
	                                CheckObjectType.EDUCATION,
	                                education
	                        );

	                // Compute document types once
	              //  List<DocumentTypeVerificationDTO> documentTypes = buildDocumentTypes(education.getId(), check, fields);
	                
	                List<DocumentTypeVerificationDTO> documentTypes = buildDocumentTypes(education.getId(), check, null);


	                // Compute object status from document types
	                DocumentStatus objectStatus =
	                        resolveObjectStatus(documentTypes);

	                return ObjectDTO.builder()
	                        .objectId(education.getId())
	                        .objectType(CheckObjectType.EDUCATION.name())
	                        .displayName(resolveEducationName(education))
	                        // .data(buildEducationData(education)) // not needed anymore
	                        .status(objectStatus.name())
	                        .documentTypes(documentTypes)
	                        .evidence(Collections.emptyList())
	                        .actions(VendorActionCatalog.objectActions())
	                        .fields(fields)
	                        .build();
	            })
	            .toList();
	            
	            */
	}


	private String resolveEducationName(EducationHistory education) {

		String degree = education.getDegree() != null ? education.getDegree().getName() : "Education";

		String field = education.getField() != null ? education.getField().getName() : "";

		if (!field.isBlank()) {
			return degree + " - " + field;
		}

		return degree;
	}

	private Map<String, Object> buildEducationData(EducationHistory education) {

		Map<String, Object> data = new HashMap<>();

		data.put("degree", education.getDegree() != null ? education.getDegree().getName() : null);

		data.put("fieldOfStudy", education.getField() != null ? education.getField().getName() : null);

		data.put("instituteName", education.getInstituteName());
		data.put("universityName", education.getUniversityName());

		data.put("fromDate", education.getFromDate());
		data.put("toDate", education.getToDate());
		data.put("yearOfPassing", education.getYearOfPassing());

		data.put("typeOfEducation", education.getTypeOfEducation());

		data.put("grade", education.getGrade());
		data.put("gpa", education.getGpa());
		data.put("description", education.getDescription());

		data.put("city", education.getCity());
		data.put("state", education.getState());
		data.put("country", education.getCountry());

		data.put("verified", education.isVerified());
		data.put("verificationStatus", education.getVerificationStatus());
		data.put("verifiedBy", education.getVerifiedBy());

		data.put("createdAt", education.getCreatedAt());
		data.put("updatedAt", education.getUpdatedAt());

		return data;
	}

	private List<ObjectDTO> buildWorkExperienceObjects(
	        VerificationCaseCheck check) {

	    Long caseId =
	            check.getVerificationCase().getCaseId();
	                 

	    List<VerificationCaseSelection> selections =
	            verificationCaseSelectionRepository
	                    .findByVerificationCase_CaseIdAndType(
	                            caseId,
	                            CheckCategoryEnum.WORK_EXPERIENCE
	                    );

	    List<Long> experienceIds =
	            selections.stream()
	                    .map(VerificationCaseSelection::getReferenceId)
	                    .toList();

	    List<WorkExperience> experiences =
	            workExperienceRepository.findAllById(experienceIds);
	    
	    List<VerificationObject> objects =
	            verificationObjectRepository
	                    .findByVerificationCheckAndObjectType(
	                            check,
	                            CheckCategoryEnum.WORK_EXPERIENCE);
	    
	    
	    
	     return objects.stream()
	            .map(object -> {

	                List<ObjectComparisonFieldDTO> fields = buildComparisonFields(object);
	                
	                List<DocumentTypeVerificationDTO> documentTypes =
	                        buildDocumentTypes(
	                        		object.getSourceId(),
	                                check,
	                                null
	                        );

	                return ObjectDTO.builder()
	                        .objectId(object.getSourceId())
	                        .objectType(object.getObjectType().name())
	                        .displayName(object.getObjectName())
	                        .status(object.getStatus().name())
	                        .fields(fields)
	                        .fieldSatusOptions(getComparisonStatuses())
	                        .documentTypes(documentTypes)
	                        .build();

	            })
	            .toList();
/*
	    return experiences.stream()
	            .map(experience -> {

	                List<ObjectFieldDTO> fields =
	                        fieldBuilderRegistry.resolveFields(
	                                CheckObjectType.WORK_EXPERIENCE,
	                                experience
	                        );

	                List<DocumentTypeVerificationDTO> documentTypes =
	                        buildDocumentTypes(
	                                experience.getExperienceId(),
	                                check,
	                                null
	                        );

	                
	                DocumentStatus objectStatus =
	                        resolveObjectStatus(documentTypes);

	                return ObjectDTO.builder()
	                        .objectId(experience.getExperienceId())
	                        .objectType(CheckObjectType.WORK_EXPERIENCE.name())
	                        .displayName(resolveWorkExperienceName(experience))
	                        .status(objectStatus.name())
	                        .documentTypes(documentTypes)
	                        .fields(fields)
	                        .build();
	            })
	            .toList();
	            
	            */
	}


	private String resolveWorkExperienceName(WorkExperience experience) {

		String company = experience.getCompanyName();
		String position = experience.getPosition();

		if (company != null && position != null) {
			return position + " at " + company;
		}

		return company != null ? company : "Work Experience";
	}

	private Map<String, Object> buildWorkExperienceData(WorkExperience experience) {

		Map<String, Object> data = new HashMap<>();

		data.put("companyName", experience.getCompanyName());
		data.put("position", experience.getPosition());

		data.put("startDate", experience.getStartDate());
		data.put("endDate", experience.getEndDate());
		data.put("currentlyWorking", experience.getCurrentlyWorking());

		data.put("employmentType", experience.getEmploymentType());
		data.put("noticePeriod", experience.getNoticePeriod());

		data.put("employeeId", experience.getEmployeeId());
		data.put("reasonForLeaving", experience.getReason());

		data.put("managerEmail", experience.getManagerEmailId());
		data.put("hrEmail", experience.getHrEmailId());

		data.put("address", experience.getAddress());
		data.put("city", experience.getCity());
		data.put("state", experience.getState());
		data.put("country", experience.getCountry());

		// Derived values
		data.put("durationInMonths", experience.getDurationInMonths());
		data.put("durationInYears", experience.getDurationInYears());

		// Verification fields
		data.put("verified", experience.isVerified());
		data.put("verificationStatus", experience.getVerificationStatus());
		data.put("verifiedBy", experience.getVerifiedBy());

		data.put("createdAt", experience.getCreatedAt());
		data.put("updatedAt", experience.getUpdatedAt());

		return data;
	}

	private List<DocumentTypeVerificationDTO> buildDocumentTypes(Long objectId, VerificationCaseCheck check,List<ObjectFieldDTO> fields

	) {

		List<Document> documents = documentRepository
				.findByCandidate_CandidateIdAndVerificationCaseCheck_CaseCheckIdAndObjectIdAndStatusNot(
						check.getVerificationCase().getCandidateId(), check.getCaseCheckId(), objectId,
						DocumentStatus.DELETED);

		Map<Object, List<Document>> grouped = documents.stream()
				.collect(Collectors.groupingBy(doc -> doc.getDocTypeId().getDocTypeId()));

		return grouped.entrySet().stream().map(entry -> {
			DocumentType docType = entry.getValue().get(0).getDocTypeId();

			return DocumentTypeVerificationDTO.builder()
					.documentTypeId(String.valueOf(docType.getDocTypeId()))
					.type(docType.getLabel())
					.status(resolveDocumentTypeStatus(entry.getValue()))
					// .actions(VendorActionCatalog.documentActions())
					 .fields(fields)
					 .actions(resolveDocumentActions(resolveDocumentTypeStatus(entry.getValue()),check.getStatus()))
					.files(buildVerificationFiles(entry.getValue(),check)).build();
		}).toList();

	}

	private String resolveDocumentTypeStatus(List<Document> documents) {

	    if (documents.stream().anyMatch(d -> d.getStatus() == DocumentStatus.REJECTED)) {
	        return DocumentStatus.REJECTED.name();
	    }

	    if (documents.stream().anyMatch(d -> d.getStatus() == DocumentStatus.REQUEST_INFO)) {
	        return DocumentStatus.REQUEST_INFO.name();
	    }

	    if (documents.stream().anyMatch(d -> d.getStatus() == DocumentStatus.INSUFFICIENT)) {
	        return DocumentStatus.INSUFFICIENT.name();
	    }

	    if (documents.stream().allMatch(Document::isVerified)) {
	        return DocumentStatus.VERIFIED.name();
	    }

	    return DocumentStatus.PENDING.name();
	}


	private List<VerificationFileDTO> buildVerificationFiles(
	        List<Document> documents,
	        VerificationCaseCheck check
	) {

	    return documents.stream()
	            .filter(doc -> doc.getStatus() != DocumentStatus.DELETED)
	            .filter(doc -> !Boolean.FALSE.equals(doc.getActive()))   // only active (true or null)
	            .map(doc -> VerificationFileDTO.builder()
	                    .docId(doc.getDocId())
	                    .fileId(doc.getDocId())
	                    .fileName(doc.getOriginalFileName())
	                    .fileUrl(doc.getFileUrl())
	                    .fileSize(doc.getFileSize())
	                    .fileType(doc.getFileType())
	                    .status(doc.getStatus())
	                    .uploadedBy(doc.getUploadedBy())
	                    .uploadedAt(doc.getUploadedAt())
	                    .verified(doc.isVerified())
	                    .comments(doc.getComments())
	                    .createdAt(doc.getCreatedAt())
	                    .updatedAt(doc.getUpdatedAt())
	                    .fileKey(doc.getAwsDocKey())
	                    .actions(resolveFileActions(doc.getStatus(), check))
	                    .build())
	            .toList();
	}


	private List<EvidenceTypeDTO> getAllowedEvidenceTypes(Long categoryId) {
		return categoryEvidenceTypeRepository.findByCategoryCategoryIdAndActiveTrue(categoryId).stream()
				.map(CategoryEvidenceType::getEvidenceType) // extract EvidenceType
				.map(this::toDTO).toList();

	}

	private EvidenceTypeDTO toDTO(EvidenceType et) {
		return EvidenceTypeDTO.builder().id(et.getId()).name(et.getLabel()).value(et.getCode()).build();
	}

	private List<DocumentTypeInfo> getDocumentTypesForCategory(VerificationCaseCheck check) {
		// Long categoryId = check.getCategory().getId(); // get category ID from check

		return verificationCaseDocumentRepository
				.findByVerificationCaseCaseIdAndCheckCategoryCategoryId(check.getVerificationCase().getCaseId(),
						check.getCategory().getCategoryId())
				.stream().map(VerificationCaseDocument::getDocumentType) // extract DocumentType
				.map(this::toDocumentTypeInfo).toList(); // Java 16+, otherwise use Collectors.toList()
	}

	private DocumentTypeInfo toDocumentTypeInfo(DocumentType dt) {
		return DocumentTypeInfo.builder().docTypeId(dt.getDocTypeId()).name(dt.getName()).code(dt.getCode())
				// .price(dt.getPrice())
				.build();
	}

	

	

	// Common operations for all check types
	@Transactional
	public void updateCheckStatus(Long checkId, Long vendorId, String status, String notes) {
		VerificationCaseCheck check = verificationCaseCheckRepository.findById(checkId)
				.orElseThrow(() -> new RuntimeException("Verification check not found"));

		if (!check.getVendorId().equals(vendorId)) {
			throw new RuntimeException("Vendor not authorized");
		}

		String currentStatus = mapCheckStatus(check.getStatus());
		CaseCheckStatus newStatus = mapToCaseStatus(status);

		check.setStatus(newStatus);
		check.setUpdatedAt(LocalDateTime.now());

		saveVerificationHistory(check, currentStatus, status, vendorId, notes);
		saveTimelineEvent(check, "status_update", String.format("Status changed to %s", status), "Vendor Agent");

		verificationCaseCheckRepository.save(check);
		updateParentCaseStatus(check.getVerificationCase());
	}

	@Transactional
	public void addNote(Long checkId, Long vendorId, String content, VendorNoteType noteType) {
		VerificationCaseCheck check = verificationCaseCheckRepository.findById(checkId)
				.orElseThrow(() -> new RuntimeException("Verification check not found"));

		if (!check.getVendorId().equals(vendorId)) {
			throw new RuntimeException("Vendor not authorized");
		}

		VendorNote note = VendorNote.builder().verificationCaseCheck(check).content(content).createdBy("Vendor Agent")
				.createdAt(LocalDateTime.now()).type(noteType).isInternal(noteType.equals("internal")).build();

		vendorNoteRepository.save(note);
		saveTimelineEvent(check, "note_added", "Note added", "Vendor Agent");
	}

	/*
	 * @Transactional public void uploadEvidence(Long checkId, Long vendorId,
	 * EvidenceDTO evidenceDTO) { VerificationCaseCheck check =
	 * verificationCaseCheckRepository.findById(checkId) .orElseThrow(() -> new
	 * RuntimeException("Verification check not found"));
	 * 
	 * if (!check.getVendorId().equals(vendorId)) { throw new
	 * RuntimeException("Vendor not authorized"); }
	 * 
	 * VerificationEvidence evidence = VerificationEvidence.builder()
	 * .verificationCaseCheck(check) // .type(evidenceDTO.getType()) //
	 * .source(evidenceDTO.getSource()) // .verifiedBy(evidenceDTO.getVerifiedBy())
	 * // .verifiedAt(LocalDateTime.now()) // .status(evidenceDTO.getStatus()) //
	 * .notes(evidenceDTO.getNotes()) //
	 * .evidencePath(evidenceDTO.getEvidencePath()) .createdAt(LocalDateTime.now())
	 * .build();
	 * 
	 * evidenceRepository.save(evidence);
	 * 
	 * String eventDescription = String.format("%s evidence uploaded: %s",
	 * check.getCategory().getCode(), evidenceDTO.getType());
	 * saveTimelineEvent(check, "evidence_uploaded", eventDescription,
	 * "Vendor Agent"); }
	 */
	@Transactional
	public void completeCheck(Long checkId, Long vendorId, String finalStatus, String summary) {
		VerificationCaseCheck check = verificationCaseCheckRepository.findById(checkId)
				.orElseThrow(() -> new RuntimeException("Verification check not found"));

		if (!check.getVendorId().equals(vendorId)) {
			throw new RuntimeException("Vendor not authorized");
		}

		CaseCheckStatus status = "verified".equals(finalStatus) ? CaseCheckStatus.COMPLETED
				: "insufficient".equals(finalStatus) ? CaseCheckStatus.INSUFFICIENT : CaseCheckStatus.IN_PROGRESS;

		check.setStatus(status);
		check.setUpdatedAt(LocalDateTime.now());

		addNote(checkId, vendorId, String.format("Check completed. Status: %s. Summary: %s", finalStatus, summary),
				VendorNoteType .VERIFICATION);

		String eventDescription = String.format("%s verification completed: %s", check.getCategory().getCode(),
				finalStatus);
		saveTimelineEvent(check, "verification_completed", eventDescription, "Vendor Agent");

		verificationCaseCheckRepository.save(check);
		updateParentCaseStatus(check.getVerificationCase());
	}

	// Common helper methods (same as before but more generic)
	private CandidateInfoDTO mapCandidateInfo(Candidate candidate) {

	    if (candidate == null) {
	        return CandidateInfoDTO.builder()
	                .name("Unknown Candidate")
	                .build();
	    }

	  //  Profile profile = candidate.getProfile();

	    String name = "Profile Pending";
	    String phone = null;

	    /*
	    if (profile != null) {
	        name = Stream.of(profile.getFirstName(), profile.getLastName())
	                .filter(Objects::nonNull)
	                .collect(Collectors.joining(" "));
	        phone = profile.getPhoneNumber();
	    }
*/
	    return CandidateInfoDTO.builder()
	            .candidateId(String.valueOf(candidate.getCandidateId()))
	            .candidateRef(candidate.getCandidateRef())
	            .name(name.isBlank() ? "Profile Pending" : name)
	            .phone(phone)
	            .build();
	}


	private EmployerInfoDTO mapEmployerInfo(Company company) {
		if (company == null) {
			return EmployerInfoDTO.builder().companyName("Unknown Company").build();
		}

		return EmployerInfoDTO.builder().name(company.getCompanyName()).companyName(company.getCompanyName())
				// .email(company.getEmail())
				// .phone(company.getPhone())
				.companyId(String.valueOf(company.getId())).build();
	}

	@Transactional(readOnly = true)
	public List<VerificationDocumentDTO> getDocuments(VerificationCaseCheck check) {
		// First, get the VerificationCaseDocument records for this case and check type
		List<VerificationCaseDocument> caseDocuments = verificationCaseDocumentRepository
				.findByVerificationCaseCaseIdAndCheckCategoryCategoryId(check.getVerificationCase().getCaseId(),
						check.getCategory().getCategoryId());

		return caseDocuments.stream().flatMap(caseDoc -> getLinkedDocuments(caseDoc).stream())
				.map(this::mapToDocumentDTO).collect(Collectors.toList());
	}

	private List<Document> getLinkedDocuments(VerificationCaseDocument caseDocument) {
		// Get all linked Document entities through VerificationCaseDocumentLink
		List<VerificationCaseDocumentLink> links = verificationCaseDocumentLinkRepository
				.findByCaseDocument(caseDocument);

		return links.stream().map(VerificationCaseDocumentLink::getDocument).filter(Objects::nonNull)
				.filter(doc -> doc.getStatus() != DocumentStatus.DELETED).collect(Collectors.toList());
	}

	private VerificationDocumentDTO mapToDocumentDTO(Document document) {
		return VerificationDocumentDTO.builder().id(document.getDocId()).name(buildDisplayName(document)) // ✅ PAN Card
																											// (abc.jpg)
				.type(getDocumentTypeName(document)).size(formatFileSize(document.getFileSize()))
				.uploadedBy(document.getUploadedBy()).uploadedAt(document.getUploadedAt())
				.status(mapDocumentStatus(document)).url(document.getFileUrl()).documentPath(document.getAwsDocKey())
				.mimeType(document.getFileType()).verificationNotes(document.getVerificationNotes())
				.comments(document.getComments()).verified(document.isVerified()).verifiedAt(document.getVerifiedAt())
				.verifiedBy(document.getVerifiedBy()).createdAt(document.getCreatedAt())
				.updatedAt(document.getUpdatedAt()).build();
	}

	private String buildDisplayName(Document document) {
		String docType = getDocumentTypeName(document); // PAN Card
		String fileName = document.getOriginalFileName() != null ? document.getOriginalFileName()
				: document.getFileName();

		if (docType != null && fileName != null) {
			return docType + " (" + fileName + ")";
		}

		return fileName != null ? fileName : docType;
	}

	private String getDocumentTypeName(Document document) {
		if (document.getDocTypeId() != null) {
			return document.getDocTypeId().getName(); // Assuming DocumentType has a 'name' field
		}
		return document.getFileType();
	}

	private String mapDocumentStatus(Document document) {
		if (document.isVerified()) {
			return "verified";
		}
		return document.getStatus() != null ? document.getStatus().name() : DocumentStatus.UPLOADED.name();
	}

	private String formatFileSize(Long bytes) {
		if (bytes == null)
			return "0 B";

		if (bytes < 1024) {
			return bytes + " B";
		} else if (bytes < 1024 * 1024) {
			return String.format("%.1f KB", bytes / 1024.0);
		} else {
			return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
		}
	}

	// Other helper methods remain similar but are now generic
	private String mapCheckStatus(CaseCheckStatus status) {
		if (status == null)
			return "pending";
		switch (status) {
		case COMPLETED:
			return "verified";
		case IN_PROGRESS:
			return "in_progress";
		case PENDING:
			return "pending";
		case ON_HOLD:
			return "on_hold";
		// case DELAYED: return "delayed";
		 case INSUFFICIENT: return "insufficient";
		default:
			return "pending";
		}
	}

	private CaseCheckStatus mapToCaseStatus(String status) {
		switch (status) {
		case "verified":
		case "completed":
			return CaseCheckStatus.COMPLETED;
		case "in_progress":
			return CaseCheckStatus.IN_PROGRESS;
		// case "pending": return CaseStatus.PENDING;
		// case "on_hold": return CaseStatus.ON_HOLD;
		// case "delayed": return CaseStatus.DELAYED;
		// case "insufficient": return CaseStatus.INSUFFICIENT;
		default:
			return CaseCheckStatus.IN_PROGRESS;
		}
	}

	private List<TimelineEventDTO> getTimeline(VerificationCaseCheck check) {
		return timelineRepository.findByVerificationCaseCheckOrderByTimestampAsc(check).stream()
				.map(tl -> TimelineEventDTO.builder().id("TL-" + tl.getTimelineId()).action(tl.getAction())
						.description(tl.getDescription()).performedBy(tl.getPerformedBy()).timestamp(tl.getTimestamp())
						.icon(getIconForAction(tl.getAction())).build())
				.collect(Collectors.toList());
	}

	private List<VerificationHistoryDTO> getVerificationHistory(VerificationCaseCheck check) {
		return historyRepository.findByVerificationCaseCheckOrderByTimestampDesc(check).stream()
				.map(h -> VerificationHistoryDTO.builder().id("VH-" + h.getHistoryId()).action(h.getAction())
						.fromStatus(h.getFromStatus()).toStatus(h.getToStatus()).performedBy(h.getPerformedBy())
						.timestamp(h.getTimestamp()).notes(h.getNotes()).build())
				.collect(Collectors.toList());
	}

	private List<RequirementDTO> getRequirements(VerificationCaseCheck check) {
		// In real implementation, get from requirement entity
		// For now, return mock requirements based on check type
		List<RequirementDTO> requirements = new ArrayList<>();

		if ("education".equalsIgnoreCase(check.getCategory().getCode())) {
			requirements.add(RequirementDTO.builder().id("REQ-001").requirement("Degree Certificate").status("provided")
					.mandatory(true).build());

			requirements.add(RequirementDTO.builder().id("REQ-002").requirement("Marksheets (All Semesters)")
					.status("provided").mandatory(true).build());

			requirements.add(RequirementDTO.builder().id("REQ-003").requirement("University Verification")
					.status("in_progress").mandatory(true).build());
		}

		return requirements;
	}

	private SlaInfoDTO getSlaInfo(VerificationCaseCheck check) {
		LocalDateTime assignedDate = check.getCreatedAt();
		LocalDateTime dueDate = assignedDate.plusDays(7); // 7-day SLA
		long daysRemaining = ChronoUnit.DAYS.between(LocalDateTime.now(), dueDate);

		String slaStatus = "on_track";
		if (daysRemaining <= 0) {
			slaStatus = "breached";
		} else if (daysRemaining <= 2) {
			slaStatus = "at_risk";
		}

		return SlaInfoDTO.builder().assignedDate(assignedDate).dueDate(dueDate)
				.daysRemaining(daysRemaining > 0 ? daysRemaining : 0).status(slaStatus)
				.completedAt(check.getStatus() == CaseCheckStatus.COMPLETED ? check.getUpdatedAt() : null).build();
	}
/*
	private List<VendorNoteDTO> getVendorNotes(VerificationCaseCheck check) {
		return vendorNoteRepository.findByVerificationCaseCheck(check).stream()
				.map(note -> VendorNoteDTO.builder().id("NOTE-" + note.getNoteId()).content(note.getContent())
						.createdBy(note.getCreatedBy()).createdAt(note.getCreatedAt()).type(note.getType())
						.isInternal(note.isInternal()).build())
				.collect(Collectors.toList());
	}
	
	*/
	@Transactional(readOnly = true)
	public List<VendorNoteDTO> getVendorNotes(
	        Long checkId,
	        Long vendorId) {

	    VerificationCaseCheck check =
	            verificationCaseCheckRepository
	                    .findById(checkId)
	                    .orElseThrow(() ->
	                            new RuntimeException("Verification check not found"));

	    // Optional ownership validation
	    if (!check.getVendorId().equals(vendorId)) {
	        throw new RuntimeException(
	                "Vendor not authorized for this verification check");
	    }

	    return vendorNoteRepository
	            .findByVerificationCaseCheck_CaseCheckIdOrderByCreatedAtDesc(checkId)
	            .stream()
	            .map(this::toDto)
	            .toList();
	}

	private String getIconForAction(String action) {
		switch (action) {
		case "check_assigned":
			return "assignment";
		case "documents_uploaded":
			return "upload";
		case "verification_initiated":
			return "play_arrow";
		case "evidence_collected":
		case "status_updated":
		case "verification_completed":
			return "check_circle";
		case "note_added":
			return "note";
		default:
			return "info";
		}
	}

	private void saveVerificationHistory(VerificationCaseCheck check, String fromStatus, String toStatus,
			Long performedById, String notes) {
		VerificationCheckHistory history = VerificationCheckHistory.builder().verificationCaseCheck(check)
				.action("status_update").fromStatus(fromStatus).toStatus(toStatus).performedBy("Vendor Agent") // Get
																												// from
																												// vendor
																												// details
				.performedById(performedById).timestamp(LocalDateTime.now()).notes(notes).build();

		historyRepository.save(history);
	}
	/*
	 * private List<EvidenceDTO> getEvidence(VerificationCaseCheck check) { return
	 * evidenceRepository.findByVerificationCaseCheck(check) .stream()
	 * .map(this::mapToEvidenceDTO) .toList(); }
	 */

	/*
	 * private EvidenceDTO mapToEvidenceDTO(VerificationEvidence ev) {
	 * 
	 * return EvidenceDTO.builder()
	 * 
	 * ===== Identity ===== .evidenceId(ev.getId())
	 * 
	 * ===== Classification ===== .categoryId(getId(ev.getCategory()))
	 * .docTypeId(getId(ev.getDocumentType())) .objectId(ev.getObjectId())
	 * .level(ev.getEvidenceLevel())
	 * 
	 * ===== Evidence Type ===== // If you introduce EvidenceType entity later, plug
	 * it here .evidenceTypeId(null) .evidenceTypeCode(null)
	 * .evidenceTypeLabel(null)
	 * 
	 * ===== File Info ===== .fileName(ev.getFileName())
	 * .originalFileName(ev.getOriginalFileName()) .fileType(ev.getFileType())
	 * .fileSize(ev.getFileSize()) .evidencePath(ev.getFileUrl())
	 * 
	 * ===== Verification ===== .status(ev.getStatus()) .verifiedBy(null)
	 * .verifiedAt(null) .notes(ev.getRemarks())
	 * 
	 * ===== Audit ===== .uploadedBy(ev.getUploadedById())
	 * .uploadedAt(ev.getUploadedAt())
	 * 
	 * .build(); }
	 */

	private Long getId(Object entity) {
		if (entity == null)
			return null;

		if (entity instanceof CheckCategory c)
			return c.getCategoryId();
		if (entity instanceof DocumentType d)
			return d.getDocTypeId();

		return null;
	}

	private String getCaseReference(VerificationCase verificationCase) {
		// Generate or fetch case reference
		// For now, use pattern CASE-{id}
		return "CASE-" + verificationCase.getCaseId();
	}

	private void saveTimelineEvent(VerificationCaseCheck check, String action, String description, String performedBy) {
		VerificationTimeline timeline = VerificationTimeline.builder().verificationCaseCheck(check).action(action)
				.description(description).performedBy(performedBy).timestamp(LocalDateTime.now()).build();

		timelineRepository.save(timeline);
	}

	private void updateParentCaseStatus(VerificationCase verificationCase) {
		// Check if all checks are completed
		List<VerificationCaseCheck> checks = verificationCase.getCaseChecks();
		boolean allCompleted = checks.stream().allMatch(check -> check.getStatus() == CaseCheckStatus.COMPLETED
				|| check.getStatus() == CaseCheckStatus.INSUFFICIENT);

		boolean anyInProgress = checks.stream().anyMatch(check -> check.getStatus() == CaseCheckStatus.IN_PROGRESS);

		if (allCompleted) {
			verificationCase.setStatus(CaseStatus.COMPLETED);
			verificationCase.setCompletedAt(LocalDateTime.now());
		} else if (anyInProgress) {
			verificationCase.setStatus(CaseStatus.IN_PROGRESS);
		}

		verificationCase.setUpdatedAt(LocalDateTime.now());
		verificationCaseRepository.save(verificationCase);
	}

	@Transactional
	public void addVendorNote(Long checkId, Long vendorId, String content, VendorNoteType  noteType) {
		log.info("Adding note to check {} by vendor {}", checkId, vendorId);

		VerificationCaseCheck check = verificationCaseCheckRepository.findById(checkId)
				.orElseThrow(() -> new RuntimeException("Verification check not found"));

		// Verify vendor has access
		if (!check.getVendorId().equals(vendorId)) {
			throw new RuntimeException("Vendor not authorized to add notes");
		}

		VendorNote note = VendorNote.builder()
				.verificationCaseCheck(check)
				.content(content)
				.createdBy("Vendor Agent") 
				.createdAt(LocalDateTime.now())
				.type(noteType)
				.isInternal(noteType.equals("internal"))
				.build();

		vendorNoteRepository.save(note);

		// Add timeline event
		saveTimelineEvent(check, "note_added", "Vendor added a note", "Vendor Agent");
	}

	/**
	 * Mark requirement as completed
	 */
	@Transactional
	public void updateRequirementStatus(Long checkId, Long vendorId, String requirementId, String status,
			String notes) {
		log.info("Updating requirement {} for check {} to {}", requirementId, checkId, status);

		// In a real implementation, you would have a Requirement entity
		// For now, we'll just update the check and add a note
		VerificationCaseCheck check = verificationCaseCheckRepository.findById(checkId)
				.orElseThrow(() -> new RuntimeException("Verification check not found"));

		// Verify vendor has access
		if (!check.getVendorId().equals(vendorId)) {
			throw new RuntimeException("Vendor not authorized to update requirements");
		}

		// Add a note about requirement update
		addVendorNote(checkId, vendorId,
				String.format("Requirement %s marked as %s. Notes: %s", requirementId, status, notes), VendorNoteType .VERIFICATION);

		// Add timeline event
		saveTimelineEvent(check, "requirement_updated",
				String.format("Requirement %s updated to %s", requirementId, status), "Vendor Agent");
	}

	/**
	 * Complete verification check
	 */
	@Transactional
	public void completeVerification(Long checkId, Long vendorId, String finalStatus, String summary) {
		log.info("Completing verification check {} by vendor {}", checkId, vendorId);

		VerificationCaseCheck check = verificationCaseCheckRepository.findById(checkId)
				.orElseThrow(() -> new RuntimeException("Verification check not found"));

		// Verify vendor has access
		if (!check.getVendorId().equals(vendorId)) {
			throw new RuntimeException("Vendor not authorized to complete verification");
		}

		// Update status
		CaseCheckStatus status = "verified".equals(finalStatus) ? CaseCheckStatus.COMPLETED
				: "insufficient".equals(finalStatus) ? CaseCheckStatus.INSUFFICIENT : CaseCheckStatus.IN_PROGRESS;

		check.setStatus(status);
		check.setUpdatedAt(LocalDateTime.now());

		// Add final note
		addVendorNote(checkId, vendorId,
				String.format("Verification completed. Final status: %s. Summary: %s", finalStatus, summary),
				VendorNoteType .VERIFICATION);

		// Add timeline event
		saveTimelineEvent(check, "verification_completed",
				String.format("Verification completed with status: %s", finalStatus), "Vendor Agent");

		verificationCaseCheckRepository.save(check);

		// Update parent case
		updateParentCaseStatus(check.getVerificationCase());
	}

	
	private List<ActionDTO> resolveDocumentActions(String documentTypeStatus,CaseCheckStatus checkStatus) {

		boolean restricted =
		        Set.of("REQUEST_INFO", "INSUFFICIENT").contains(documentTypeStatus)
		        || CaseCheckStatus.ACTION_REQUIRED.equals(checkStatus);
	    
		if (!restricted) {
	        return VendorActionCatalog.documentActions();
	    }

	    // 🔒 Only allow view + download
	    return VendorActionCatalog.documentActions().stream()
	            .map(action -> {
	                if (action.getCode() == ActionType.VIEW ||
	                    action.getCode() == ActionType.DOWNLOAD) {
	                    return action;
	                }

	                return ActionDTO.builder()
	                        .code(action.getCode())
	                        .label(action.getLabel())
	                        .level(action.getLevel())
	                        .enabled(false)
	                        .build();
	            })
	            .toList();
	}

	private List<ActionDTO> resolveFileActions(DocumentStatus status,VerificationCaseCheck check) {

		log.info("resolveFileActions::::::::::::::::::::::::::::::{}",status);
		
		boolean restricted =
		        status == DocumentStatus.REQUEST_INFO ||
		        status == DocumentStatus.INSUFFICIENT ||
		        status == DocumentStatus.REJECTED ||
		        status == DocumentStatus.VERIFIED ||
		        check.getStatus() == CaseCheckStatus.REJECTED ;
		      //  || check.getStatus() == CaseCheckStatus.ACTION_REQUIRED;
		
		
		log.info("resolveFileActions::::::::::::::::::restricted::::::::::::{}",restricted);
	    if (!restricted) {
	        return VendorActionCatalog.documentActions();
	    }

	    // 🔒 Only allow view + download
	    return VendorActionCatalog.documentActions().stream()
	            .map(action -> {
	                if (action.getCode() == ActionType.VIEW ||
	                    action.getCode() == ActionType.DOWNLOAD) {
	                    return action;
	                }

	                return ActionDTO.builder()
	                        .code(action.getCode())
	                        .label(action.getLabel())
	                        .level(action.getLevel())
	                        .enabled(false)
	                        .build();
	            })
	            .toList();
	}
	
	private DocumentStatus resolveObjectStatus(List<DocumentTypeVerificationDTO> docTypes) {

	    if (docTypes.stream().anyMatch(d -> d.getStatus().equals("REJECTED"))) {
	        return DocumentStatus.REJECTED;
	    }

	    if (docTypes.stream().anyMatch(d -> d.getStatus().equals("REQUEST_INFO"))) {
	        return DocumentStatus.REQUEST_INFO;
	    }

	    if (docTypes.stream().anyMatch(d -> d.getStatus().equals("INSUFFICIENT"))) {
	        return DocumentStatus.INSUFFICIENT;
	    }

	    if (docTypes.stream().allMatch(d -> d.getStatus().equals("VERIFIED"))) {
	        return DocumentStatus.VERIFIED;
	    }

	    return DocumentStatus.PENDING;
	}
	
	
	private List<ActionDTO> resolveCheckActions(CaseCheckStatus status) {

	    boolean restricted = switch (status) {
	        case 
	             VERIFIED,
	             REJECTED,
	             FAILED,
	             ESCALATED,
	             ACTION_REQUIRED -> true;
	        default -> false;
	    };

	    if (!restricted) {
	        return VendorActionCatalog.checkActions();
	    }

	    // 🔒 Read-only
	    return VendorActionCatalog.checkActions().stream()
	    		.map(action -> {
	                if (action.getCode() == ActionType.VIEW ||
	                    action.getCode() == ActionType.DOWNLOAD) {
	                    return action;
	                }

	                return ActionDTO.builder()
	                        .code(action.getCode())
	                        .label(action.getLabel())
	                        .level(action.getLevel())
	                        .enabled(false)
	                        .build();
	            })
	            .toList();
	}
	
	
	
	private List<ActionDTO> resolveObjectActions(CaseCheckStatus checkStatus) {

	    boolean restricted = checkStatus == CaseCheckStatus.ACTION_REQUIRED
	            || checkStatus == CaseCheckStatus.VERIFIED
	            || checkStatus == CaseCheckStatus.REJECTED;

	    if (!restricted) {
	        return VendorActionCatalog.objectActions();
	    }

	    return VendorActionCatalog.objectActions().stream()
	            .map(action -> {
	                if (action.getCode() == ActionType.VIEW ||
	                    action.getCode() == ActionType.DOWNLOAD) {
	                    return action;
	                }

	                return ActionDTO.builder()
	                        .code(action.getCode())
	                        .label(action.getLabel())
	                        .level(action.getLevel())
	                        .enabled(false)
	                        .build();
	            })
	            .toList();
	}

	
	private VendorNoteDTO toDto(VendorNote entity) {

	    if (entity == null) {
	        return null;
	    }

	    VendorNoteDTO dto = new VendorNoteDTO();

	    dto.setNoteId(entity.getNoteId());
	    dto.setContent(entity.getContent());
	    dto.setCreatedBy(entity.getCreatedBy());
	    dto.setCreatedAt(entity.getCreatedAt());
	    dto.setType(entity.getType());

	    dto.setVisibleToEmployer(entity.getVisibleToEmployer());
	    dto.setVisibleToCandidate(entity.getVisibleToCandidate());

	    dto.setInternal(entity.isInternal());

	    return dto;
	}
	
	
	private List<ObjectComparisonFieldDTO> buildComparisonFields(VerificationObject object) {

	    return verificationFieldComparisonRepository
	            .findByVerificationObjectOrderById(object)
	            .stream()
	            .map(field ->

	                    ObjectComparisonFieldDTO.builder()
	                            .comparisonId(field.getId())
	                            .fieldName(field.getFieldName())
	                            .displayName(field.getDisplayName())
	                            .candidateValue(field.getCandidateValue())
	                            .sourceValue(field.getSourceValue())
	                            .result(field.getResult().name())
	                            .verified(field.getVerified())
	                            .remarks(field.getRemarks())
	                            .build()

	            )
	            .toList();
	}
	

	public List<OptionDTO> getComparisonStatuses() {

        return Arrays.stream(ComparisonStatus.values())
                .map(status ->
                        new OptionDTO(
                                status.name(),
                                status.getLabel(),
                                status.getColor()))
                .toList();
    }
	
	
	
}
