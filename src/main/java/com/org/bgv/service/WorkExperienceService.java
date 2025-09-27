package com.org.bgv.service;

import com.org.bgv.controller.ProfileController;
import com.org.bgv.dto.DocumentResponse;
import com.org.bgv.dto.DocumentStats;
import com.org.bgv.dto.DocumentSummary;
import com.org.bgv.dto.WorkExperienceDTO;
import com.org.bgv.dto.WorkExperienceResponse;
import com.org.bgv.entity.BaseDocument;
import com.org.bgv.entity.ProfessionalDocuments;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.WorkExperience;
import com.org.bgv.repository.ProfessionalDocumentsRepository;
import com.org.bgv.repository.ProfileRepository;
import com.org.bgv.repository.WorkExperienceRepository;
import com.org.bgv.s3.S3StorageService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkExperienceService {

	private final WorkExperienceRepository workExperienceRepository;
	private final ProfileRepository profileRepository;
	private final ProfessionalDocumentsRepository professionalDocumentsRepository;
	private final S3StorageService s3StorageService;
	
	private static final Logger logger = LoggerFactory.getLogger(WorkExperienceService.class);

	@Transactional
	public List<WorkExperienceDTO> saveWorkExperiences(List<WorkExperienceDTO> workExperienceDTOs, Long profileId) {
		Profile profile = profileRepository.findById(profileId)
				.orElseThrow(() -> new RuntimeException("Profile not found: " + profileId));

		// Delete existing work experiences for this profile (optional - if you want to
		// replace all)
		// workExperienceRepository.deleteByProfile_ProfileId(profileId);

		// Convert DTOs to entities and save
		List<WorkExperience> workExperiences = workExperienceDTOs.stream().map(dto -> mapToEntity(dto, profile))
				.collect(Collectors.toList());

		List<WorkExperience> savedExperiences = workExperienceRepository.saveAll(workExperiences);

		// Convert saved entities back to DTOs with IDs
		return savedExperiences.stream().map(this::mapToDTO).collect(Collectors.toList());
	}

	public List<WorkExperienceDTO> getWorkExperiencesByProfile(Long profileId) {
		List<WorkExperience> experiences = workExperienceRepository.findByProfile_ProfileId(profileId);
		return experiences.stream().map(this::mapToDTO).collect(Collectors.toList());
	}

	public WorkExperienceResponse getWorkExperiencesWithDocuments(Long profileId) {
		// Verify profile exists
		logger.info("IN WORKEXPERIENCE SERVICE::::::::::::START");
		Profile profile = profileRepository.findById(profileId)
				.orElseThrow(() -> new EntityNotFoundException("Profile not found with ID: " + profileId));

		// Get all work experiences for the profile
		List<WorkExperience> workExperiences = workExperienceRepository.findByProfile_ProfileId(profileId);

		if (workExperiences.isEmpty()) {
			return WorkExperienceResponse.builder().profileId(profileId)
					// .profileName(profile.getFirstName() + " " + profile.getLastName())
					.workExperiences(Collections.emptyList()).summary(new DocumentSummary(0, 0, 0, 0, 0, 0)).build();
		}

		// Get all professional documents for these experiences
		List<Long> experienceIds = workExperiences.stream().map(WorkExperience::getExperienceId)
				.collect(Collectors.toList());

		List<ProfessionalDocuments> allDocuments = professionalDocumentsRepository
				.findByProfile_ProfileIdAndObjectIdIn(profileId, experienceIds);

		logger.info("allDocuments:::::::::::size::{}", allDocuments == null ? 0 : allDocuments);
		logger.info("allDocuments:::::::::::::{}", allDocuments);

		// Create response
		List<WorkExperienceDTO> experienceDetails = workExperiences.stream()
				.map(experience -> convertToWorkExperienceDetail(experience, allDocuments))
				.collect(Collectors.toList());

		// Calculate summary
	//	DocumentSummary summary = calculateSummary(experienceDetails);

		return WorkExperienceResponse.builder().profileId(profileId)
				// .profileName(profile.getFirstName() + " " + profile.getLastName())
				.workExperiences(experienceDetails)
				// .summary(summary)
				.build();
	}

	private WorkExperience mapToEntity(WorkExperienceDTO dto, Profile profile) {
		return WorkExperience.builder().profile(profile).company_name(dto.getCompanyName()).position(dto.getPosition())
				.start_date(dto.getStartDate()).end_date(dto.getEndDate()).reason(dto.getReasonForLeaving())
				.employee_id(dto.getEmployeeId()).manager_email_id(dto.getManagerEmailId())
				.hr_email_id(dto.getHrEmailId()).address(dto.getAddress()).build();
	}

	private WorkExperienceDTO mapToDTO(WorkExperience entity) {
		return WorkExperienceDTO.builder().experienceId(entity.getExperienceId())
				// .profileId(entity.getProfile() != null ? entity.getProfile().getProfileId() :
				// null)
				.companyName(entity.getCompany_name()).position(entity.getPosition()).startDate(entity.getStart_date())
				.endDate(entity.getEnd_date()).reasonForLeaving(entity.getReason()).employeeId(entity.getEmployee_id())
				.managerEmailId(entity.getManager_email_id()).hrEmailId(entity.getHr_email_id())
				.address(entity.getAddress()).build();
	}

	private WorkExperienceDTO convertToWorkExperienceDetail(WorkExperience experience,
			List<ProfessionalDocuments> documents) {
		// Filter documents for this specific experience
		List<DocumentResponse> documentResponses = documents.stream()
				.filter(doc -> doc.getObjectId() != null && doc.getObjectId().equals(experience.getExperienceId()))
				.map(this::convertToDocumentResponse).collect(Collectors.toList());

		DocumentStats stats = calculateDocumentStats(documentResponses);

		return WorkExperienceDTO.builder().experienceId(experience.getExperienceId())
				.companyName(experience.getCompany_name()).position(experience.getPosition())
				.startDate(experience.getStart_date()).endDate(experience.getEnd_date())
				.employeeId(experience.getEmployee_id()).managerEmailId(experience.getManager_email_id())
				.hrEmailId(experience.getHr_email_id()).address(experience.getAddress())
				.reasonForLeaving(experience.getReason()).documents(documentResponses)
				// .documentStats(stats)
				.build();
	}

	private DocumentResponse convertToDocumentResponse(ProfessionalDocuments document) {

		return DocumentResponse.builder().doc_id(document.getDoc_id())
				.category_id(document.getCategory().getCategoryId()).category_name(document.getCategory().getName())
				.doc_type_id(document.getType_id().getDoc_type_id()).document_type_name(document.getType_id().getName())
				.file_url(document.getFile_url()).file_size(document.getFile_size())
				.file_name(extractFileName(document.getFile_url())).file_type(extractFileType(document.getFile_url()))
				.status(document.getStatus()).uploadedAt(document.getUploadedAt()).verifiedAt(document.getVerifiedAt())
				.comments(document.getComments()).awsDocKey(document.getAwsDocKey()).build();
	}

	private DocumentStats calculateDocumentStats(List<DocumentResponse> documents) {
		long total = documents.size();
		long verified = documents.stream().filter(doc -> "VERIFIED".equals(doc.getStatus())).count();
		long pending = documents.stream().filter(doc -> "PENDING".equals(doc.getStatus())).count();
		long rejected = documents.stream().filter(doc -> "REJECTED".equals(doc.getStatus())).count();

		return new DocumentStats(total, verified, pending, rejected);
	}
/*
	private DocumentSummary calculateSummary(List<WorkExperienceDTO> experiences) {
		int totalCompanies = experiences.size();
		long totalDocuments = experiences.stream().mapToLong(e -> e.getDocumentStats().getTotalDocuments()).sum();
		long verified = experiences.stream().mapToLong(e -> e.getDocumentStats().getVerified()).sum();
		long pending = experiences.stream().mapToLong(e -> e.getDocumentStats().getPending()).sum();
		long rejected = experiences.stream().mapToLong(e -> e.getDocumentStats().getRejected()).sum();

		int completionPercentage = totalDocuments > 0 ? (int) ((verified * 100) / totalDocuments) : 0;

		return new DocumentSummary(totalCompanies, totalDocuments, verified, pending, rejected, completionPercentage);
	}
*/
	private String extractFileName(String fileUrl) {
		if (fileUrl == null)
			return null;
		return fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
	}

	private String extractFileType(String fileUrl) {
		if (fileUrl == null)
			return "UNKNOWN";
		int dotIndex = fileUrl.lastIndexOf(".");
		return dotIndex > 0 ? fileUrl.substring(dotIndex + 1).toUpperCase() : "UNKNOWN";
	}
	
	
	public void deleteWorkexperience(Long profileId) {
		
		List<ProfessionalDocuments> allDocuments = professionalDocumentsRepository.findByProfile_ProfileId(profileId);
		 for (ProfessionalDocuments document : allDocuments) {
	            if (document.getAwsDocKey() != null) {
	                s3StorageService.deleteFile(document.getAwsDocKey());
	            }
	        }
		professionalDocumentsRepository.deleteByProfile_ProfileId(profileId); 
		
		workExperienceRepository.deleteByProfile_ProfileId(profileId);
		
	}
	
	public List<WorkExperienceDTO> updateWorkExperiences(List<WorkExperienceDTO> workExperienceDTOs, Long profileId) {
        // Validate profile exists
		List<WorkExperienceDTO> workList = new ArrayList<>();
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new EntityNotFoundException("Profile not found with id: " + profileId));

        // Validate input
        if (workExperienceDTOs == null || workExperienceDTOs.isEmpty()) {
            throw new IllegalArgumentException("Work experiences list cannot be empty");
        }
        
        for(WorkExperienceDTO workExperienceDTO:workExperienceDTOs) {
        	
        	 List<WorkExperience> workExperiences = workExperienceRepository.findByExperienceId(workExperienceDTO.getExperienceId());
        	 WorkExperience workExperience = workExperiences.get(0);
        	 workExperience.setProfile(profile);
        	 workExperience.setAddress(workExperienceDTO.getAddress());
        	 workExperience.setCompany_name(workExperienceDTO.getCompanyName());
        	 workExperience.setEmployee_id(workExperienceDTO.getEmployeeId());
        	 workExperience.setEnd_date(workExperienceDTO.getEndDate());
        	 workExperience.setHr_email_id(workExperienceDTO.getHrEmailId());
        	 workExperience.setManager_email_id(workExperienceDTO.getManagerEmailId());
        	 workExperience.setReason(workExperienceDTO.getReasonForLeaving());
        	 workExperience.setStart_date(workExperienceDTO.getStartDate());
        	 workExperience = workExperienceRepository.save(workExperience);
        	 
        	 WorkExperienceDTO workDto = mapToDTO(workExperience);
        	 workList.add(workDto);
        }
        return workList;
        
    }
}
