package com.org.bgv.service;

import com.org.bgv.candidate.entity.WorkExperience;
import com.org.bgv.candidate.repository.WorkExperienceRepository;
import com.org.bgv.controller.ProfileController;
import com.org.bgv.dto.DocumentResponse;
import com.org.bgv.dto.DocumentStats;
import com.org.bgv.dto.DocumentSummary;
import com.org.bgv.dto.WorkExperienceDTO;
import com.org.bgv.dto.WorkExperienceResponse;
import com.org.bgv.entity.BaseDocument;
import com.org.bgv.entity.ProfessionalDocuments;
import com.org.bgv.entity.Profile;
import com.org.bgv.repository.ProfessionalDocumentsRepository;
import com.org.bgv.repository.ProfileRepository;
import com.org.bgv.s3.S3StorageService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
		return WorkExperience.builder()
				.profile(profile)
				.company_name(dto.getCompanyName())
				.position(dto.getPosition())
				.start_date(dto.getStartDate()).end_date(dto.getEndDate()).reason(dto.getReasonForLeaving())
				.employee_id(dto.getEmployeeId()).manager_email_id(dto.getManagerEmail())
				.hr_email_id(dto.getHrEmail()).address(dto.getCompanyAddress())
				.city(dto.getCity())
				.state(dto.getState())
				.country(dto.getCountry())
				.currentlyWorking(dto.getCurrentlyWorking())
				.noticePeriod(dto.getNoticePeriod())
				.employmentType(dto.getEmploymentType())
				.build();
	}

	private WorkExperienceDTO mapToDTO(WorkExperience entity) {
		return WorkExperienceDTO.builder().id(entity.getExperienceId())
				// .profileId(entity.getProfile() != null ? entity.getProfile().getProfileId() :
				// null)
				.companyName(entity.getCompany_name()).position(entity.getPosition()).startDate(entity.getStart_date())
				.endDate(entity.getEnd_date()).reasonForLeaving(entity.getReason()).employeeId(entity.getEmployee_id())
				.managerEmail(entity.getManager_email_id()).hrEmail(entity.getHr_email_id())
				.companyAddress(entity.getAddress())
				
				.city(entity.getCity())
				.state(entity.getState())
				.country(entity.getCountry())
				.currentlyWorking(entity.getCurrentlyWorking())
				.noticePeriod(entity.getNoticePeriod())
				.employmentType(entity.getEmploymentType())
				.build();
	}

	private WorkExperienceDTO convertToWorkExperienceDetail(WorkExperience experience,
			List<ProfessionalDocuments> documents) {
		// Filter documents for this specific experience
		List<DocumentResponse> documentResponses = documents.stream()
				.filter(doc -> doc.getObjectId() != null && doc.getObjectId().equals(experience.getExperienceId()))
				.map(this::convertToDocumentResponse).collect(Collectors.toList());

		DocumentStats stats = calculateDocumentStats(documentResponses);

		return WorkExperienceDTO.builder().id(experience.getExperienceId())
				.companyName(experience.getCompany_name()).position(experience.getPosition())
				.startDate(experience.getStart_date()).endDate(experience.getEnd_date())
				.employeeId(experience.getEmployee_id()).managerEmail(experience.getManager_email_id())
				.hrEmail(experience.getHr_email_id()).companyAddress(experience.getAddress())
				.reasonForLeaving(experience.getReason()).documents(documentResponses)
				// .documentStats(stats)
				.build();
	}

	private DocumentResponse convertToDocumentResponse(ProfessionalDocuments document) {

		return DocumentResponse.builder().doc_id(document.getDocId())
				.category_id(document.getCategory().getCategoryId()).category_name(document.getCategory().getName())
				.doc_type_id(document.getDocTypeId().getDocTypeId()).document_type_name(document.getDocTypeId().getName())
				.file_url(document.getFileUrl()).file_size(document.getFileSize())
				.file_name(extractFileName(document.getFileUrl())).file_type(extractFileType(document.getFileUrl()))
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
	
	public void deleteWorkExperience(Long profileId, Long experienceId) {
	    workExperienceRepository.findByProfile_ProfileIdAndExperienceId(profileId, experienceId)
	            .ifPresentOrElse(
	                    workExperienceRepository::delete,
	                    () -> {
	                        throw new EntityNotFoundException(
	                            "Work experience not found for profileId: " + profileId + " and experienceId: " + experienceId
	                        );
	                    }
	            );
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
        
        for(WorkExperienceDTO workExperienceDTO : workExperienceDTOs) {
            try {
            	 WorkExperience workExperience = null;
            	
             	if(workExperienceDTO.getId()!=null) {
             		workExperience = workExperienceRepository.findByProfile_ProfileIdAndExperienceId(profileId, workExperienceDTO.getId())
                             .orElseThrow(() -> new RuntimeException("Work experience history not found: " + workExperienceDTO.getId()));
             	}else {
             		workExperience = new WorkExperience();
             	}
            	
                                          
                   
                    workExperience.setProfile(profile);
                    workExperience.setAddress(workExperienceDTO.getCompanyAddress());
                    workExperience.setCompany_name(workExperienceDTO.getCompanyName());
                    workExperience.setEmployee_id(workExperienceDTO.getEmployeeId());
                    workExperience.setEnd_date(workExperienceDTO.getEndDate());
                    workExperience.setHr_email_id(workExperienceDTO.getHrEmail());
                    workExperience.setManager_email_id(workExperienceDTO.getManagerEmail());
                    workExperience.setReason(workExperienceDTO.getReasonForLeaving());
                    workExperience.setStart_date(workExperienceDTO.getStartDate());
                    
                    // Set other fields that might be missing
                    workExperience.setPosition(workExperienceDTO.getPosition());
                    workExperience.setEmploymentType(workExperienceDTO.getEmploymentType());
                    workExperience.setCurrentlyWorking(workExperienceDTO.getCurrentlyWorking());
                    workExperience.setNoticePeriod(workExperienceDTO.getNoticePeriod());
                    workExperience.setCity(workExperienceDTO.getCity());
                    workExperience.setState(workExperienceDTO.getState());
                    workExperience.setCountry(workExperienceDTO.getCountry());
                    
                    workExperience = workExperienceRepository.save(workExperience);
                    WorkExperienceDTO workDto = mapToDTO(workExperience);
                    workList.add(workDto);
                
            } catch (Exception e) {
                // Log the error and handle appropriately
                System.err.println("Error updating work experience: " + e.getMessage());
                throw e; // or handle differently based on your requirements
            }
        }
        return workList;
        
    }
	
	
	
	// verification
	@Cacheable(value = "workExperience", key = "#candidateId")
    public List<WorkExperienceDTO> getExperiences(Long candidateId) {
        logger.info("Fetching work experience records for candidate: {}", candidateId);
        
        List<WorkExperience> experiences = workExperienceRepository.findByCandidateIdOrderByDate(candidateId);
        
        return experiences.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
	private WorkExperienceDTO convertToDTO(WorkExperience experience) {
        WorkExperienceDTO dto = new WorkExperienceDTO();
        dto.setId(experience.getExperienceId());
        dto.setCandidateId(experience.getCandidateId());
        dto.setCompanyName(experience.getCompany_name());
        dto.setPosition(experience.getPosition());
        dto.setEmploymentType(experience.getEmploymentType());
        dto.setStartDate(experience.getStart_date());
        dto.setEndDate(experience.getEnd_date());
        dto.setCurrentlyWorking(experience.getCurrentlyWorking());
        dto.setEmployeeId(experience.getEmployee_id());
        dto.setManagerEmail(experience.getManager_email_id());
        dto.setHrEmail(experience.getHr_email_id());
        dto.setReasonForLeaving(experience.getReason());
        dto.setNoticePeriod(experience.getNoticePeriod());
        dto.setCompanyAddress(experience.getAddress());
        dto.setCity(experience.getCity());
        dto.setState(experience.getState());
        dto.setCountry(experience.getCountry());
        dto.setVerified(experience.isVerified());
        dto.setVerificationStatus(experience.getVerificationStatus());
        dto.setVerifiedBy(experience.getVerifiedBy());
        return dto;
    }
}
