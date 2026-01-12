package com.org.bgv.vendor.service;

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
import com.org.bgv.common.DocumentTypeInfo;
import com.org.bgv.constants.CaseCheckStatus;
import com.org.bgv.constants.CaseStatus;
import com.org.bgv.entity.*;
import com.org.bgv.repository.*;
import com.org.bgv.vendor.action.dto.ActionDTO;
import com.org.bgv.vendor.action.dto.VendorActionCatalog;
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
import com.org.bgv.vendor.dto.ObjectDTO;
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
//import com.org.bgv.vendor.entity.VerificationEvidence;
import com.org.bgv.vendor.entity.VerificationTimeline;
import com.org.bgv.vendor.repository.CategoryEvidenceTypeRepository;
import com.org.bgv.vendor.repository.EvidenceTypeRepository;
import com.org.bgv.vendor.repository.VendorNoteRepository;
import com.org.bgv.vendor.repository.VerificationCheckHistoryRepository;
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

	/**
	 * Get verification check details by type
	 */
	/*
	 * @Transactional(readOnly = true) public VendorVerificationCheckDTO
	 * getVerificationCheck(Long checkId, Long vendorId) {
	 * 
	 * log.info("Fetching verification check {} for vendor {}", checkId, vendorId);
	 * 
	 * VerificationCaseCheck check =
	 * verificationCaseCheckRepository.findById(checkId) .orElseThrow(() -> new
	 * RuntimeException("Verification check not found"));
	 * 
	 * // 1️⃣ Vendor authorization if (!Objects.equals(check.getVendorId(),
	 * vendorId)) { throw new
	 * RuntimeException("Vendor not authorized to access this check"); }
	 * 
	 * VerificationCase verificationCase = check.getVerificationCase();
	 * 
	 * Candidate candidate =
	 * candidateRepository.findById(verificationCase.getCandidateId())
	 * .orElseThrow(() -> new RuntimeException("Candidate not found"));
	 * 
	 * Company company =
	 * companyRepository.findById(verificationCase.getCompanyId()).orElse(null);
	 * 
	 * // 2️⃣ Base DTO VendorVerificationCheckDTO dto = buildCommonCheckDTO(check,
	 * verificationCase, candidate, company);
	 * 
	 * // 3️⃣ Declared + Context dto.setDeclaredInfo(getDeclaredInfo(check,
	 * candidate)); dto.setContext(getCheckContext(check,
	 * check.getCategory().getCode()));
	 * 
	 * // 4️⃣ Evidence already uploaded dto.setEvidence(getEvidence(check));
	 * 
	 * // 5️⃣ Evidence types allowed for this category
	 * dto.setEvidenceTypeList(getAllowedEvidenceTypes(check.getCategory().
	 * getCategoryId()));
	 * 
	 * // 6️⃣ Document types applicable for this category
	 * dto.setDocumentTypeInfos(getDocumentTypesForCategory(check));
	 * 
	 * return dto; }
	 */

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
				.checkType(check.getCategory().getCode().toLowerCase())
				.checkName(check.getCategory().getName())
				.status(mapCheckStatus(check.getStatus()))
				.candidate(mapCandidateInfo(candidate))
				// .audit(buildAudit(check))
				 .actions(VendorActionCatalog.checkActions()) 
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

		return identities.stream()
				.map(identity -> ObjectDTO.builder()
						.objectId(identity.getId())
						.objectType("IDENTITY")
						.displayName(resolveIdentityName(identity))
						.data(buildIdentityData(identity))
						.documentTypes(buildDocumentTypes(identity.getId(), check

						)).evidence(Collections.emptyList())
						.actions(VendorActionCatalog.objectActions())
						.build()).toList();
	}

	private String resolveIdentityName(IdentityProof identityProof) {

		DocumentType documentType = documentTypeRepository.findById(identityProof.getDocTypeId())
				.orElseThrow(() -> new RuntimeException("Not found"));

		return documentType.getLabel();

	}

	private Map<String, Object> buildIdentityData(IdentityProof identity) {

		Map<String, Object> data = new HashMap<>();
		data.put("documentNumber", identity.getDocumentNumber());
		data.put("issueDate", identity.getIssueDate());
		data.put("expiryDate", identity.getExpiryDate());
		data.put("verified", identity.getVerified());
		data.put("verificationStatus", identity.getVerificationStatus());
		data.put("verifiedBy", identity.getVerifiedBy());
		data.put("verifiedAt", identity.getVerifiedAt());
		data.put("expired", identity.isExpired());
		data.put("daysUntilExpiry", identity.getDaysUntilExpiry());

		return data;
	}

	private List<ObjectDTO> buildEducationObjects(VerificationCaseCheck check) {

		List<EducationHistory> educations = educationHistoryRepository
				.findByVerificationCaseCheck_CaseCheckId(check.getCaseCheckId());

		return educations.stream()
				.map(education -> ObjectDTO.builder()
						.objectId(education.getId())
						.objectType("EDUCATION")
						.displayName(resolveEducationName(education))
						.data(buildEducationData(education))
						.documentTypes(buildDocumentTypes(education.getId(), check))
						.evidence(Collections.emptyList())
						.actions(VendorActionCatalog.objectActions())
						.build())
				.toList();
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

		data.put("instituteName", education.getInstitute_name());
		data.put("universityName", education.getUniversity_name());

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

	private List<ObjectDTO> buildWorkExperienceObjects(VerificationCaseCheck check) {

		List<WorkExperience> experiences = workExperienceRepository
				.findByVerificationCaseCheck_CaseCheckId(check.getCaseCheckId());

		return experiences.stream()
				.map(experience -> ObjectDTO.builder().objectId(experience.getExperienceId())
						.objectType("WORK_EXPERIENCE")
						.displayName(resolveWorkExperienceName(experience))
						.data(buildWorkExperienceData(experience))
						.documentTypes(buildDocumentTypes(experience.getExperienceId(), check))
						.actions(VendorActionCatalog.objectActions())
						.evidence(Collections.emptyList()).build())
				.toList();
	}

	private String resolveWorkExperienceName(WorkExperience experience) {

		String company = experience.getCompany_name();
		String position = experience.getPosition();

		if (company != null && position != null) {
			return position + " at " + company;
		}

		return company != null ? company : "Work Experience";
	}

	private Map<String, Object> buildWorkExperienceData(WorkExperience experience) {

		Map<String, Object> data = new HashMap<>();

		data.put("companyName", experience.getCompany_name());
		data.put("position", experience.getPosition());

		data.put("startDate", experience.getStart_date());
		data.put("endDate", experience.getEnd_date());
		data.put("currentlyWorking", experience.getCurrentlyWorking());

		data.put("employmentType", experience.getEmploymentType());
		data.put("noticePeriod", experience.getNoticePeriod());

		data.put("employeeId", experience.getEmployee_id());
		data.put("reasonForLeaving", experience.getReason());

		data.put("managerEmail", experience.getManager_email_id());
		data.put("hrEmail", experience.getHr_email_id());

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

	private List<DocumentTypeVerificationDTO> buildDocumentTypes(Long objectId, VerificationCaseCheck check

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
					 .actions(VendorActionCatalog.documentActions())
					// .actions(resolveDocumentActions(resolveDocumentTypeStatus(entry.getValue())))
					.files(buildVerificationFiles(entry.getValue())).build();
		}).toList();

	}

	private String resolveDocumentTypeStatus(List<Document> documents) {

		if (documents.stream().allMatch(Document::isVerified)) {
			return "VERIFIED";
		}

		if (documents.stream().anyMatch(d -> d.getStatus() == DocumentStatus.REJECTED)) {
			return "REJECTED";
		}

		if (documents.stream().anyMatch(d -> d.getStatus() == DocumentStatus.INSUFFICIENT)) {
			return "INSUFFICIENT";
		}
		if (documents.stream().anyMatch(d -> d.getStatus() == DocumentStatus.REQUEST_INFO)) {
			return "REQUEST_INFO";
		}
		

		return "PENDING";
	}

	private List<VerificationFileDTO> buildVerificationFiles(List<Document> documents) {

		return documents.stream()
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
						//.verifiedBy(doc.getVerifiedBy())
						//.verifiedAt(doc.getVerifiedAt())
						//.verificationNotes(doc.getVerificationNotes())
						.comments(doc.getComments())
						.createdAt(doc.getCreatedAt())
						.updatedAt(doc.getUpdatedAt())
						.fileKey(doc.getAwsDocKey())
						.actions(resolveFileActions(doc.getStatus()))
						.status(doc.getStatus())
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

	/**
	 * Get type-specific verification check
	 */
	@Transactional(readOnly = true)
	public Object getTypeSpecificCheck(Long checkId, Long vendorId) {
		VerificationCaseCheck check = verificationCaseCheckRepository.findById(checkId)
				.orElseThrow(() -> new RuntimeException("Verification check not found"));

		if (!check.getVendorId().equals(vendorId)) {
			throw new RuntimeException("Vendor not authorized");
		}

		String checkType = check.getCategory().getCode().toLowerCase();

		switch (checkType) {
		case "education":
			return getEducationCheck(check, vendorId);
		case "employment":
			return getEmploymentCheck(check, vendorId);
		case "identity":
			return getIdentityCheck(check, vendorId);
		// case "criminal":
		// return getCriminalCheck(check, vendorId);
		// case "reference":
		// return getReferenceCheck(check, vendorId);
		default:
			throw new RuntimeException("Unsupported check type: " + checkType);
		}
	}

	private EducationCheckDTO getEducationCheck(VerificationCaseCheck check, Long vendorId) {
		VerificationCase verificationCase = check.getVerificationCase();
		Candidate candidate = candidateRepository.findById(verificationCase.getCandidateId())
				.orElseThrow(() -> new RuntimeException("Candidate not found"));

		// Get candidate's declared education info (from candidate_education table)
		// For now, mock data
		DeclaredEducationInfoDTO declaredInfo = DeclaredEducationInfoDTO.builder().degree("Bachelor of Technology")
				.institution("XYZ University").specialization("Computer Science").yearOfPassing("2020")
				.percentage("78.5%").rollNumber("CS2017001").duration("2016-2020").location("Delhi, India")
				.declaredOn(LocalDateTime.now().minusDays(2)).build();

		EducationContextDTO context = EducationContextDTO.builder().verificationStage("university_contact")
				.universityContactDetails("registrar@xyzuniversity.edu.in")
				.portalUrl("https://xyzuniversity.edu.in/verification")
				.verificationChannels(Arrays.asList("email", "portal")).difficultyLevel("medium").isInternational(false)
				.build();

		return EducationCheckDTO.builder().declaredInfo(declaredInfo).context(context)
				.checkId("CHK-EDU-" + check.getCaseCheckId()).caseId(String.valueOf(verificationCase.getCaseId()))
				.checkType("education").status(mapCheckStatus(check.getStatus())).candidate(mapCandidateInfo(candidate))
				.documents(getDocuments(check)).timeline(getTimeline(check)).requirements(getRequirements(check))
				.slas(getSlaInfo(check)).vendorNotes(getVendorNotes(check))
				//.evidence(getEvidence(check))
				.verificationHistory(getVerificationHistory(check)).build();
	}

	private EmploymentCheckDTO getEmploymentCheck(VerificationCaseCheck check, Long vendorId) {
		VerificationCase verificationCase = check.getVerificationCase();
		Candidate candidate = candidateRepository.findById(verificationCase.getCandidateId())
				.orElseThrow(() -> new RuntimeException("Candidate not found"));

		// Get candidate's declared employment info (from candidate_employment table)
		DeclaredEmploymentInfoDTO declaredInfo = DeclaredEmploymentInfoDTO.builder().companyName("ABC Technologies")
				.designation("Senior Software Engineer").employmentType("full_time")
				.startDate(LocalDateTime.now().minusYears(2)).endDate(LocalDateTime.now().minusMonths(1))
				.isCurrentEmployment(false).location("Bangalore, India").employeeId("EMP12345")
				.declaredOn(LocalDateTime.now().minusDays(3)).verificationMethod("email").build();

		EmploymentContextDTO context = EmploymentContextDTO.builder().verificationStage("hr_contact")
				.hrContactDetails("hr@abctech.com").companyWebsite("https://abctech.com").isCompanyActive(true)
				.verificationMethod("email").requiresReferenceCheck(true).companySize("1000-5000")
				.industryType("IT Services").build();

		return EmploymentCheckDTO.builder().declaredInfo(declaredInfo).context(context)
				.checkId("CHK-EMP-" + check.getCaseCheckId()).caseId(String.valueOf(verificationCase.getCaseId()))
				.checkType("employment").status(mapCheckStatus(check.getStatus()))
				.candidate(mapCandidateInfo(candidate)).documents(getDocuments(check)).timeline(getTimeline(check))
				.requirements(getRequirements(check)).slas(getSlaInfo(check)).vendorNotes(getVendorNotes(check))
				//.evidence(getEvidence(check))
				.verificationHistory(getVerificationHistory(check)).build();
	}

	private IdentityCheckDTO getIdentityCheck(VerificationCaseCheck check, Long vendorId) {
		VerificationCase verificationCase = check.getVerificationCase();
		Candidate candidate = candidateRepository.findById(verificationCase.getCandidateId())
				.orElseThrow(() -> new RuntimeException("Candidate not found"));

		DeclaredIdentityInfoDTO declaredInfo = DeclaredIdentityInfoDTO.builder().documentType("aadhaar")
				.documentNumber("XXXX-XXXX-1234")
				.name(candidate.getProfile().getFirstName() + " " + candidate.getProfile().getLastName())
				.dateOfBirth(candidate.getProfile().getDateOfBirth()).gender(candidate.getProfile().getGender())
				// .address(candidate.getProfile().getAddress())
				.declaredOn(LocalDateTime.now().minusDays(1)).build();

		IdentityContextDTO context = IdentityContextDTO.builder().verificationStage("document_authentication")
				.verificationAuthority("UIDAI").isDigitalVerificationPossible(true)
				.acceptedDocuments(Arrays.asList("aadhaar", "passport", "driving_license"))
				.requiresPhysicalVerification(false).governmentPortalUrl("https://myaadhaar.uidai.gov.in").build();

		return IdentityCheckDTO.builder().declaredInfo(declaredInfo).context(context)
				.checkId("CHK-ID-" + check.getCaseCheckId()).caseId(String.valueOf(verificationCase.getCaseId()))
				.checkType("identity").status(mapCheckStatus(check.getStatus())).candidate(mapCandidateInfo(candidate))
				.documents(getDocuments(check)).timeline(getTimeline(check)).requirements(getRequirements(check))
				.slas(getSlaInfo(check))
				.vendorNotes(getVendorNotes(check))
				// .evidence(getEvidence(check))
				.verificationHistory(getVerificationHistory(check)).build();
	}

	// Helper methods
	private VendorVerificationCheckDTO buildCommonCheckDTO(VerificationCaseCheck check,
			VerificationCase verificationCase, Candidate candidate, Company company) {
		return VendorVerificationCheckDTO.builder().checkId(check.getCaseCheckId()).checkRef(check.getCheckRef())
				.caseId(String.valueOf(verificationCase.getCaseId())).caseRef(getCaseReference(verificationCase))
				.checkType(check.getCategory().getCode().toLowerCase()).status(mapCheckStatus(check.getStatus()))
				.categoryName(check.getCategory().getName()).categoryCode(check.getCategory().getCode())
				.candidate(mapCandidateInfo(candidate)).employer(mapEmployerInfo(company))
				.documents(getDocuments(check)).timeline(getTimeline(check)).requirements(getRequirements(check))
				.slas(getSlaInfo(check)).vendorNotes(getVendorNotes(check))
				//.evidence(getEvidence(check))
				.verificationHistory(getVerificationHistory(check)).build();
	}

	private Map<String, Object> getDeclaredInfo(VerificationCaseCheck check, Candidate candidate) {
		String checkType = check.getCategory().getCode().toLowerCase();
		Map<String, Object> declaredInfo = new HashMap<>();

		switch (checkType) {
		case "education":
			declaredInfo.put("degree", "Bachelor of Technology");
			declaredInfo.put("institution", "XYZ University");
			declaredInfo.put("year", "2020");
			break;
		case "employment":
			declaredInfo.put("company", "ABC Technologies");
			declaredInfo.put("designation", "Senior Software Engineer");
			declaredInfo.put("duration", "2 years");
			break;
		case "identity":
			declaredInfo.put("documentType", "Aadhaar");
			declaredInfo.put("documentNumber", "XXXX-XXXX-1234");
			declaredInfo.put("name", candidate.getProfile().getFirstName());
			break;
		}

		return declaredInfo;
	}

	private Map<String, Object> getCheckContext(VerificationCaseCheck check, String checkType) {
		Map<String, Object> context = new HashMap<>();
		context.put("verificationStage", "in_progress");
		context.put("assignedTo", "Vendor Agent");
		context.put("lastUpdated", check.getUpdatedAt());

		switch (checkType) {
		case "education":
			context.put("universityContact", "registrar@xyzuniversity.edu.in");
			context.put("verificationMethod", "portal");
			break;
		case "employment":
			context.put("hrContact", "hr@company.com");
			context.put("verificationMethod", "email");
			break;
		case "identity":
			context.put("verificationMethod", "document_authentication");
			context.put("requiresBiometric", false);
			break;
		}

		return context;
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
	public void addNote(Long checkId, Long vendorId, String content, String noteType) {
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
				"verification");

		String eventDescription = String.format("%s verification completed: %s", check.getCategory().getCode(),
				finalStatus);
		saveTimelineEvent(check, "verification_completed", eventDescription, "Vendor Agent");

		verificationCaseCheckRepository.save(check);
		updateParentCaseStatus(check.getVerificationCase());
	}

	// Common helper methods (same as before but more generic)
	private CandidateInfoDTO mapCandidateInfo(Candidate candidate) {
		return CandidateInfoDTO.builder()
				.name(candidate.getProfile().getFirstName() + " " + candidate.getProfile().getLastName())
				.email(candidate.getProfile().getEmailAddress()).phone(candidate.getProfile().getPhoneNumber())
				.candidateId(String.valueOf(candidate.getCandidateId())).candidateRef(candidate.getCandidateRef())
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

	private List<VendorNoteDTO> getVendorNotes(VerificationCaseCheck check) {
		return vendorNoteRepository.findByVerificationCaseCheck(check).stream()
				.map(note -> VendorNoteDTO.builder().id("NOTE-" + note.getNoteId()).content(note.getContent())
						.createdBy(note.getCreatedBy()).createdAt(note.getCreatedAt()).type(note.getType())
						.isInternal(note.isInternal()).build())
				.collect(Collectors.toList());
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
	public void addVendorNote(Long checkId, Long vendorId, String content, String noteType) {
		log.info("Adding note to check {} by vendor {}", checkId, vendorId);

		VerificationCaseCheck check = verificationCaseCheckRepository.findById(checkId)
				.orElseThrow(() -> new RuntimeException("Verification check not found"));

		// Verify vendor has access
		if (!check.getVendorId().equals(vendorId)) {
			throw new RuntimeException("Vendor not authorized to add notes");
		}

		VendorNote note = VendorNote.builder().verificationCaseCheck(check).content(content).createdBy("Vendor Agent") // In
																														// real
																														// app,
																														// get
																														// from
																														// vendor
																														// details
				.createdAt(LocalDateTime.now()).type(noteType).isInternal(noteType.equals("internal")).build();

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
				String.format("Requirement %s marked as %s. Notes: %s", requirementId, status, notes), "verification");

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
				"verification");

		// Add timeline event
		saveTimelineEvent(check, "verification_completed",
				String.format("Verification completed with status: %s", finalStatus), "Vendor Agent");

		verificationCaseCheckRepository.save(check);

		// Update parent case
		updateParentCaseStatus(check.getVerificationCase());
	}

	
	private List<ActionDTO> resolveDocumentActions(String documentTypeStatus) {

	    boolean restricted =
	            "REQUEST_INFO".equals(documentTypeStatus) ||
	            "INSUFFICIENT".equals(documentTypeStatus);

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

	private List<ActionDTO> resolveFileActions(DocumentStatus status) {

		log.info("resolveFileActions::::::::::::::::::::::::::::::{}",status);
		
		boolean restricted =
		        status == DocumentStatus.REQUEST_INFO ||
		        status == DocumentStatus.INSUFFICIENT;
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

}
