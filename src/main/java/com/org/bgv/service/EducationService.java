package com.org.bgv.service;

import com.org.bgv.dto.DegreeTypeResponse;
import com.org.bgv.dto.DocumentResponse;
import com.org.bgv.dto.EducationHistoryDTO;
import com.org.bgv.dto.FieldOfStudyResponse;
import com.org.bgv.entity.EducationHistory;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.DegreeType;

//import com.org.bgv.entity.EducationDocuments;
import com.org.bgv.entity.FieldOfStudy;
import com.org.bgv.repository.EducationHistoryRepository;
import com.org.bgv.repository.ProfileRepository;
import com.org.bgv.s3.S3StorageService;

import jakarta.persistence.EntityNotFoundException;

import com.org.bgv.repository.DegreeTypeRepository;
import com.org.bgv.repository.FieldOfStudyRepository;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationService {

    private final EducationHistoryRepository educationHistoryRepository;
    private final ProfileRepository profileRepository;
    private final DegreeTypeRepository degreeTypeRepository;
    private final FieldOfStudyRepository fieldOfStudyRepository;
   // private final EducationDocumentsRepository educationDocumentsRepository;
    private final S3StorageService s3StorageService;

    private static final Logger logger = LoggerFactory.getLogger(EducationService.class);
    
    @Transactional
    public List<EducationHistoryDTO> saveEducationHistory(List<EducationHistoryDTO> educationHistoryDTOs, Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found: " + profileId));

        List<EducationHistory> educationHistories = educationHistoryDTOs.stream()
                .map(dto -> mapToEntity(dto, profile))
                .collect(Collectors.toList());

        List<EducationHistory> savedEducations = educationHistoryRepository.saveAll(educationHistories);

        return savedEducations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<EducationHistoryDTO> getEducationByProfile(Long profileId) {
        List<EducationHistory> educationHistories = educationHistoryRepository.findByProfile_ProfileId(profileId);
        List<Long> eduIds = educationHistories.stream()
                .map(EducationHistory::getId)
                .collect(Collectors.toList());

        return educationHistories.stream()
                .map(education -> convertEducationDetails(education))
                .collect(Collectors.toList());
    }

    private EducationHistory mapToEntity(EducationHistoryDTO dto, Profile profile) {
        DegreeType degree = null;
        if (dto.getQualificationType() != null) {
            degree = degreeTypeRepository.findById(dto.getQualificationType())
                    .orElseThrow(() -> new RuntimeException("Degree type not found: " + dto.getQualificationType()));
        }

        FieldOfStudy field = null;
        if (dto.getFieldOfStudy() != null) {
            field = fieldOfStudyRepository.findById(dto.getFieldOfStudy())
                    .orElseThrow(() -> new RuntimeException("Field of study not found: " + dto.getFieldOfStudy()));
        }

        return EducationHistory.builder()
                .profile(profile)
                .degree(degree)
                .field(field)
                .institute_name(dto.getInstitutionName())
                .university_name(dto.getUniversityName())
                .fromDate(parseDate(dto.getFromMonth(), dto.getFromYear()))
                .toDate(parseDate(dto.getToMonth(), dto.getToYear()))
                .city(dto.getCity())
                .state(dto.getState())
                .country(dto.getCountry())
                .yearOfPassing(dto.getYearOfPassing())
                .typeOfEducation(dto.getTypeOfEducation())
                .grade(dto.getGrade())
                .gpa(dto.getGpa())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private EducationHistoryDTO mapToDTO(EducationHistory entity) {
        return EducationHistoryDTO.builder()
                .id(entity.getId())
                .qualificationType(entity.getDegree() != null ? entity.getDegree().getDegreeId() : null)
                .degreeName(entity.getDegree() != null ? entity.getDegree().getName() : null)
                .fieldOfStudy(entity.getField() != null ? entity.getField().getFieldId() : null)
                .fieldName(entity.getField() != null ? entity.getField().getName() : null)
                .grade(entity.getGrade())
                .gpa(entity.getGpa())
                .profileId(entity.getProfile() != null ? entity.getProfile().getProfileId() : null)
                .fromMonth(entity.getFromDate() != null ? getMonthName(entity.getFromDate()) : null)
                .fromYear(entity.getFromDate() != null ? entity.getFromDate().getYear() : null)
                .toMonth(entity.getToDate() != null ? getMonthName(entity.getToDate()) : null)
                .toYear(entity.getToDate() != null ? entity.getToDate().getYear() : null)
                .institutionName(entity.getInstitute_name())
                .universityName(entity.getUniversity_name())
                .city(entity.getCity())
                .state(entity.getState())
                .country(entity.getCountry())
                .yearOfPassing(entity.getYearOfPassing())
                .typeOfEducation(entity.getTypeOfEducation())
                .build();
    }

    private LocalDate parseDate(String monthName, Integer year) {
        if (monthName == null || year == null) return null;

        try {
            Month month = Month.valueOf(monthName.toUpperCase());
            return LocalDate.of(year, month, 1);
        } catch (IllegalArgumentException e) {
            return LocalDate.of(year, 1, 1);
        }
    }

    private String getMonthName(LocalDate date) {
        if (date == null) return null;
        return date.getMonth().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.ENGLISH);
    }

    public EducationHistoryDTO updateEducationHistory(Long profileId, EducationHistoryDTO educationHistoryDTO) {
       logger.info("Education service::::::START");
    	EducationHistory existingEducation = null;
    	if(educationHistoryDTO.getId()!=null) {
    		existingEducation = educationHistoryRepository.findById(educationHistoryDTO.getId())
                    .orElseThrow(() -> new RuntimeException("Education history not found: " + educationHistoryDTO.getId()));
    	}else {
    		existingEducation = new EducationHistory();
    	}
    	 Profile profile = profileRepository.findById(profileId)
                 .orElseThrow(() -> new RuntimeException("Profile not found: " + profileId));
    	
    	existingEducation.setProfile(profile);
        existingEducation.setInstitute_name(educationHistoryDTO.getInstitutionName());
        existingEducation.setUniversity_name(educationHistoryDTO.getUniversityName());
        existingEducation.setFromDate(parseDate(educationHistoryDTO.getFromMonth(), educationHistoryDTO.getFromYear()));
        existingEducation.setToDate(parseDate(educationHistoryDTO.getToMonth(), educationHistoryDTO.getToYear()));
        existingEducation.setCity(educationHistoryDTO.getCity());
        existingEducation.setState(educationHistoryDTO.getState());
        existingEducation.setCountry(educationHistoryDTO.getCountry());
        existingEducation.setYearOfPassing(educationHistoryDTO.getYearOfPassing());
        existingEducation.setTypeOfEducation(educationHistoryDTO.getTypeOfEducation());
        existingEducation.setGrade(educationHistoryDTO.getGrade());
        existingEducation.setGpa(educationHistoryDTO.getGpa());
        existingEducation.setUpdatedAt(LocalDateTime.now());

        if (educationHistoryDTO.getQualificationType() != null) {
            DegreeType degree = degreeTypeRepository.findById(educationHistoryDTO.getQualificationType())
                    .orElseThrow(() -> new RuntimeException("Degree type not found: " + educationHistoryDTO.getQualificationType()));
            existingEducation.setDegree(degree);
        }

        if (educationHistoryDTO.getFieldOfStudy() != null) {
            FieldOfStudy field = fieldOfStudyRepository.findById(educationHistoryDTO.getFieldOfStudy())
                    .orElseThrow(() -> new RuntimeException("Field of study not found: " + educationHistoryDTO.getFieldOfStudy()));
            existingEducation.setField(field);
        }

        EducationHistory updatedEducation = educationHistoryRepository.save(existingEducation);
        return mapToDTO(updatedEducation);
    }
    
    public List<EducationHistoryDTO> updateEducationHistories(List<EducationHistoryDTO> educationHistoryDTOs, Long profileId) {
        
    	 if (educationHistoryDTOs == null || educationHistoryDTOs.isEmpty()) {
    	        throw new IllegalArgumentException("Education history list cannot be null or empty");
    	    }
    	 
    	 List<EducationHistoryDTO> updatedList = new ArrayList<>();
    	 for (EducationHistoryDTO dto : educationHistoryDTOs) {
    	       
    		 EducationHistoryDTO updateEducationHistory = updateEducationHistory(profileId, dto);
    		 
    		 updatedList.add(updateEducationHistory);
    	 }
    	return updatedList;
    }
    public void deleteEducationHistory(Long profileId, Long id) {
        educationHistoryRepository.findByProfile_ProfileIdAndId(profileId, id)
                .ifPresentOrElse(
                        educationHistoryRepository::delete,
                        () -> {
                            throw new EntityNotFoundException(
                                "Education history not found for profileId: " + profileId + " and id: " + id
                            );
                        }
                );
    }

    public void deleteAllEducationByProfile(Long profileId) {
    	/*
    	 List<EducationDocuments> allDocuments = educationDocumentsRepository.findByProfile_ProfileId(profileId);
    	 for (EducationDocuments document : allDocuments) {
             if (document.getAwsDocKey() != null) {
                 s3StorageService.deleteFile(document.getAwsDocKey());
             }
         }
         */
    	// educationDocumentsRepository.deleteByProfile_ProfileId(profileId);
        educationHistoryRepository.deleteByProfile_ProfileId(profileId);
    }

    public List<DegreeTypeResponse> getAllDegreeTypes() {
        return degreeTypeRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private DegreeTypeResponse convertToResponse(DegreeType degreeType) {
        return DegreeTypeResponse.builder()
                .degreeId(degreeType.getDegreeId())
                .name(degreeType.getName())
                .lable(degreeType.getName())
                .build();
    }

    public List<FieldOfStudyResponse> getAllFieldsOfStudy() {
        return fieldOfStudyRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private FieldOfStudyResponse convertToResponse(FieldOfStudy fieldOfStudy) {
        return FieldOfStudyResponse.builder()
                .fieldId(fieldOfStudy.getFieldId())
                .name(fieldOfStudy.getName())
                .lable(fieldOfStudy.getName())
                .build();
    }

    private EducationHistoryDTO convertEducationDetails(EducationHistory educationHistory) {
       
    	/*
    	List<DocumentResponse> documentResponses = eduDocuments.stream()
                .filter(doc -> doc.getObjectId() != null && doc.getObjectId().equals(educationHistory.getId()))
                .map(this::convertToDocumentResponse)
                .collect(Collectors.toList());
       */
        return EducationHistoryDTO.builder()
                .id(educationHistory.getId())
                .qualificationType(educationHistory.getDegree() != null ? educationHistory.getDegree().getDegreeId() : null)
                .degreeName(educationHistory.getDegree() != null ? educationHistory.getDegree().getName() : null)
                .fieldOfStudy(educationHistory.getField() != null ? educationHistory.getField().getFieldId() : null)
                .fieldName(educationHistory.getField() != null ? educationHistory.getField().getName() : null)
                .grade(educationHistory.getGrade())
                .gpa(educationHistory.getGpa())
                .profileId(educationHistory.getProfile() != null ? educationHistory.getProfile().getProfileId() : null)
                .fromMonth(educationHistory.getFromDate() != null ? getMonthName(educationHistory.getFromDate()) : null)
                .fromYear(educationHistory.getFromDate() != null ? educationHistory.getFromDate().getYear() : null)
                .toMonth(educationHistory.getToDate() != null ? getMonthName(educationHistory.getToDate()) : null)
                .toYear(educationHistory.getToDate() != null ? educationHistory.getToDate().getYear() : null)
                .institutionName(educationHistory.getInstitute_name())
                .universityName(educationHistory.getUniversity_name())
                .city(educationHistory.getCity())
                .state(educationHistory.getState())
                .country(educationHistory.getCountry())
                .yearOfPassing(educationHistory.getYearOfPassing())
                .typeOfEducation(educationHistory.getTypeOfEducation())
              //  .documents(documentResponses) // include filtered documents
                .build();
    }
    
/*
    private DocumentResponse convertToDocumentResponse(EducationDocuments document) {
        return DocumentResponse.builder()
                .doc_id(document.getDocId())
                .category_id(document.getCategory().getCategoryId())
                .category_name(document.getCategory().getName())
                .doc_type_id(document.getDocTypeId()!=null?document.getDocTypeId().getDocTypeId():0l)
                .document_type_name(document.getDocTypeId()!=null?document.getDocTypeId().getName():"")
                .file_url(document.getFileUrl())
                .file_size(document.getFileSize())
                .file_name(extractFileName(document.getFileUrl()))
                .file_type(extractFileType(document.getFileUrl()))
                .status(document.getStatus())
                .uploadedAt(document.getUploadedAt())
                .verifiedAt(document.getVerifiedAt())
                .comments(document.getComments())
                .awsDocKey(document.getAwsDocKey())
                .build();
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
}
