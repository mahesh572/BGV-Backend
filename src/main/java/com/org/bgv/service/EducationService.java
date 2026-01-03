package com.org.bgv.service;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.candidate.entity.EducationHistory;
import com.org.bgv.candidate.repository.CandidateRepository;
import com.org.bgv.candidate.repository.EducationHistoryRepository;
import com.org.bgv.dto.DegreeTypeResponse;
import com.org.bgv.dto.DocumentResponse;
import com.org.bgv.dto.EducationHistoryDTO;
import com.org.bgv.dto.FieldOfStudyResponse;
import com.org.bgv.entity.Profile;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.DegreeType;

//import com.org.bgv.entity.EducationDocuments;
import com.org.bgv.entity.FieldOfStudy;
import com.org.bgv.repository.ProfileRepository;
import com.org.bgv.repository.VerificationCaseRepository;
import com.org.bgv.s3.S3StorageService;

import jakarta.persistence.EntityNotFoundException;

import com.org.bgv.repository.DegreeTypeRepository;
import com.org.bgv.repository.FieldOfStudyRepository;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
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
    private final CandidateRepository candidateRepository;
    private final VerificationCaseRepository verificationCaseRepository;

    private static final Logger logger = LoggerFactory.getLogger(EducationService.class);
    
    @Transactional
    public List<EducationHistoryDTO> saveEducationHistory(List<EducationHistoryDTO> educationHistoryDTOs, Long candidateId,Long caseId) {
       
    	Candidate candidate = candidateRepository.findById(candidateId)
    	        .orElseThrow(() -> new RuntimeException("Profile not found: " + candidateId));
    	/*
    	Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found: " + profileId));
        */
    	
        List<EducationHistory> educationHistories = educationHistoryDTOs.stream()
                .map(dto -> mapToEntity(dto, candidate,caseId))
                .collect(Collectors.toList());

        List<EducationHistory> savedEducations = educationHistoryRepository.saveAll(educationHistories);

        return savedEducations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<EducationHistoryDTO> getEducationByProfile(Long candidateId, Long caseId) {

        List<EducationHistory> educationHistories;

        // SELF-REGISTERED / PROFILE MODE
        if (caseId == null || caseId == 0) {
            educationHistories =
                    educationHistoryRepository.findByCandidateId(candidateId);
        }
        // CASE-BASED VERIFICATION MODE
        else {
            educationHistories =
                    educationHistoryRepository
                            .findByCandidateIdAndVerificationCaseCaseId(
                                    candidateId,
                                    caseId
                            );
        }

        return educationHistories.stream()
                .map(this::convertEducationDetails)
                .collect(Collectors.toList());
    }


    private EducationHistory mapToEntity(EducationHistoryDTO dto, Candidate candidate,Long caseId) {
        DegreeType degree = null;
        VerificationCase verificationCase =null;
        if(caseId!=null && caseId!=0) {
        	verificationCase = verificationCaseRepository.findByCaseIdAndCandidateId(caseId,candidate.getCandidateId()).orElseThrow(()->new EntityNotFoundException());
        }
        
        
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
               // .profile(profile)
        		.candidateId(candidate.getCandidateId())
        		.verificationCase(verificationCase)
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

    private EducationHistoryDTO updateEducationHistory(
            Long candidateId,
            EducationHistoryDTO dto,
            Long caseId
    ) {
        EducationHistory education;

        // =====================================================
        // UPDATE
        // =====================================================
        if (dto.getId() != null) {

            // SELF PROFILE MODE
            if (caseId == null || caseId == 0) {
                education =
                    educationHistoryRepository
                        .findByCandidateIdAndId(candidateId, dto.getId())
                        .orElseThrow(() ->
                            new RuntimeException("Education not found for candidate"));
            }
            // CASE VERIFICATION MODE
            else {
                education =
                    educationHistoryRepository
                        .findByCandidateIdAndVerificationCaseCaseIdAndId(
                            candidateId,
                            caseId,
                            dto.getId()
                        );
                if (education == null) {
                    throw new RuntimeException("Education not found for case");
                }
            }
        }
        // =====================================================
        // CREATE (during update batch)
        // =====================================================
        else {
            education = new EducationHistory();
            education.setCandidateId(candidateId);

            if (caseId != null && caseId != 0) {
                VerificationCase verificationCase =
                    verificationCaseRepository.findById(caseId)
                        .orElseThrow(() -> new RuntimeException("Case not found"));
                education.setVerificationCase(verificationCase);
            }
        }

        // =====================================================
        // MAP FIELDS
        // =====================================================
        education.setInstitute_name(dto.getInstitutionName());
        education.setUniversity_name(dto.getUniversityName());
        education.setFromDate(parseDate(dto.getFromMonth(), dto.getFromYear()));
        education.setToDate(parseDate(dto.getToMonth(), dto.getToYear()));
        education.setCity(dto.getCity());
        education.setState(dto.getState());
        education.setCountry(dto.getCountry());
        education.setYearOfPassing(dto.getYearOfPassing());
        education.setTypeOfEducation(dto.getTypeOfEducation());
        education.setGrade(dto.getGrade());
        education.setGpa(dto.getGpa());

        if (dto.getQualificationType() != null) {
            education.setDegree(
                degreeTypeRepository.findById(dto.getQualificationType())
                    .orElseThrow(() -> new RuntimeException("Degree type not found"))
            );
        }

        if (dto.getFieldOfStudy() != null) {
            education.setField(
                fieldOfStudyRepository.findById(dto.getFieldOfStudy())
                    .orElseThrow(() -> new RuntimeException("Field of study not found"))
            );
        }

        return mapToDTO(educationHistoryRepository.save(education));
    }


    
    @Transactional
    public List<EducationHistoryDTO> updateEducationHistories(
            List<EducationHistoryDTO> educationHistoryDTOs,
            Long candidateId,
            Long caseId
    ) {
        if (educationHistoryDTOs == null || educationHistoryDTOs.isEmpty()) {
            throw new IllegalArgumentException("Education history list cannot be empty");
        }

        candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        List<EducationHistoryDTO> result = new ArrayList<>();

        for (EducationHistoryDTO dto : educationHistoryDTOs) {
            result.add(updateEducationHistory(candidateId, dto, caseId));
        }

        return result;
    }

    
    
    public void deleteEducationHistory(Long candidateId, Long id) {
    	
    	EducationHistory education = educationHistoryRepository
                .findByCandidateIdAndId(candidateId, id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Education history not found for candidateId: " + candidateId +
                        " and id: " + id
                ));

        educationHistoryRepository.delete(education);
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
    
    
    // verification
    
    @Cacheable(value = "education", key = "#candidateId")
    public List<EducationHistoryDTO> getEducations(Long candidateId) {
        logger.info("Fetching education records for candidate: {}", candidateId);
        
        List<EducationHistory> educations = educationHistoryRepository.findByCandidateIdOrderByDate(candidateId);
        
        return educations.stream()
            .map(this::convertEducationDetails)
            .collect(Collectors.toList());
    }
    
   
    
}
