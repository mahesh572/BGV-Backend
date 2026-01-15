package com.org.bgv.global.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.candidate.entity.Candidate;
import com.org.bgv.candidate.entity.EducationHistory;
import com.org.bgv.dto.EducationHistoryDTO;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.DegreeType;
import com.org.bgv.entity.FieldOfStudy;
import com.org.bgv.entity.User;
import com.org.bgv.entity.VerificationCase;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.global.entity.UserEducationHistory;
import com.org.bgv.global.repository.UserEducationHistoryRepository;
import com.org.bgv.repository.DegreeTypeRepository;
import com.org.bgv.repository.FieldOfStudyRepository;
import com.org.bgv.repository.ProfileRepository;
import com.org.bgv.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEducationService {

	private final UserRepository userRepository;
	private final ProfileRepository profileRepository;
    private final DegreeTypeRepository degreeTypeRepository;
    private final FieldOfStudyRepository fieldOfStudyRepository;
    private final UserEducationHistoryRepository userEducationHistoryRepository;
	
	
	@Transactional
    public List<EducationHistoryDTO> saveUserEducationHistory(List<EducationHistoryDTO> educationHistoryDTOs,Long userId) {
    	
       
    	User user = userRepository.findById(userId)
    	        .orElseThrow(() -> new RuntimeException("User not found: " + userId));
    	/*
    	Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new RuntimeException("Profile not found: " + profileId));
        */
    	
        List<UserEducationHistory> educationHistories = educationHistoryDTOs.stream()
        		
                .map(dto -> mapToEntity(dto, user))
                .collect(Collectors.toList());

        List<UserEducationHistory> savedEducations = userEducationHistoryRepository.saveAll(educationHistories);

        return savedEducations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
	
	private UserEducationHistory mapToEntity(EducationHistoryDTO dto, User user) {

	    DegreeType degree = null;
	    if (dto.getQualificationType() != null) {
	        degree = degreeTypeRepository.findById(dto.getQualificationType())
	                .orElseThrow(() ->
	                        new RuntimeException("Degree type not found: " + dto.getQualificationType()));
	    }

	    FieldOfStudy field = null;
	    if (dto.getFieldOfStudy() != null) {
	        field = fieldOfStudyRepository.findById(dto.getFieldOfStudy())
	                .orElseThrow(() ->
	                        new RuntimeException("Field of study not found: " + dto.getFieldOfStudy()));
	    }

	    return UserEducationHistory.builder()
	            .user(user)                     // ✅ GLOBAL OWNER
	           // .profile(profile)               // optional but good
	            .degree(degree)
	            .field(field)
	            .instituteName(dto.getInstitutionName())
	            .universityName(dto.getUniversityName())
	            .fromDate(parseDate(dto.getFromMonth(), dto.getFromYear()))
	            .toDate(parseDate(dto.getToMonth(), dto.getToYear()))
	            .city(dto.getCity())
	            .state(dto.getState())
	            .country(dto.getCountry())
	            .yearOfPassing(dto.getYearOfPassing())
	            .typeOfEducation(dto.getTypeOfEducation())
	            .grade(dto.getGrade())
	            .gpa(dto.getGpa())
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
	    
	    private EducationHistoryDTO mapToDTO(UserEducationHistory entity) {
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
	                .institutionName(entity.getInstituteName())
	                .universityName(entity.getUniversityName())
	                .city(entity.getCity())
	                .state(entity.getState())
	                .country(entity.getCountry())
	                .yearOfPassing(entity.getYearOfPassing())
	                .typeOfEducation(entity.getTypeOfEducation())
	                .build();
	    }
	    
	    @Transactional
	    public List<EducationHistoryDTO> updateUserEducationHistories(
	            Long userId,
	            List<EducationHistoryDTO> educationHistoryDTOs
	    ) {
	        if (educationHistoryDTOs == null || educationHistoryDTOs.isEmpty()) {
	            throw new IllegalArgumentException("Education history list cannot be empty");
	        }

	        userRepository.findById(userId)
	                .orElseThrow(() -> new RuntimeException("User not found"));

	        List<EducationHistoryDTO> result = new ArrayList();

	        for (EducationHistoryDTO dto : educationHistoryDTOs) {
	            result.add(updateUserEducationHistory(userId, dto));
	        }

	        return result;
	    }

	    private EducationHistoryDTO updateUserEducationHistory(
	    		Long userId,
	    		EducationHistoryDTO dto
	            
	    ) {
	        UserEducationHistory education;

	        // =====================================================
	        // UPDATE
	        // =====================================================
	        if (dto.getId() != null) {
	            education = userEducationHistoryRepository
	                    .findByUser_UserIdAndId(userId,dto.getId())
	                    .orElseThrow(() ->
	                            new RuntimeException("Education not found for user"));
	        }
	        // =====================================================
	        // CREATE
	        // =====================================================
	        else {
	            User user = userRepository.findById(userId)
	                    .orElseThrow(() -> new RuntimeException("User not found"));

	            education = new UserEducationHistory();
	            education.setUser(user);
	        }

	        // =====================================================
	        // MAP FIELDS
	        // =====================================================
	        education.setInstituteName(dto.getInstitutionName());
	        education.setUniversityName(dto.getUniversityName());
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
	        } else {
	            education.setDegree(null);
	        }

	        if (dto.getFieldOfStudy() != null) {
	            education.setField(
	                    fieldOfStudyRepository.findById(dto.getFieldOfStudy())
	                            .orElseThrow(() -> new RuntimeException("Field of study not found"))
	            );
	        } else {
	            education.setField(null);
	        }

	        return mapToDTO(userEducationHistoryRepository.save(education));
	    }
	    
	    public void deleteEducationHistory(Long userId, Long id) {
	    	
	    	UserEducationHistory education = userEducationHistoryRepository
	                .findByUser_UserIdAndId(userId, id)
	                .orElseThrow(() -> new EntityNotFoundException(
	                        "Education history not found for userId: " + userId +
	                        " and id: " + id
	                ));

	    	userEducationHistoryRepository.delete(education);
	    }
	    
	    public List<EducationHistoryDTO> getUserEducationHistory(Long userId) {

	        // Validate user
	        userRepository.findById(userId)
	                .orElseThrow(() -> new RuntimeException("User not found"));

	        List<UserEducationHistory> educationHistories =
	                userEducationHistoryRepository.findByUser_UserId(userId);

	        return educationHistories.stream()
	                .map(this::convertEducationDetails)
	                .collect(Collectors.toList());
	    }

	    
	    private EducationHistoryDTO convertEducationDetails(
	            UserEducationHistory education
	    ) {
	        return EducationHistoryDTO.builder()
	                .id(education.getId())

	                // Degree
	                .qualificationType(
	                        education.getDegree() != null
	                                ? education.getDegree().getDegreeId()
	                                : null
	                )
	                .degreeName(
	                        education.getDegree() != null
	                                ? education.getDegree().getName()
	                                : null
	                )

	                // Field of study
	                .fieldOfStudy(
	                        education.getField() != null
	                                ? education.getField().getFieldId()
	                                : null
	                )
	                .fieldName(
	                        education.getField() != null
	                                ? education.getField().getName()
	                                : null
	                )

	                // Scores
	                .grade(education.getGrade())
	                .gpa(education.getGpa())

	                // Dates
	                .fromMonth(
	                        education.getFromDate() != null
	                                ? getMonthName(education.getFromDate())
	                                : null
	                )
	                .fromYear(
	                        education.getFromDate() != null
	                                ? education.getFromDate().getYear()
	                                : null
	                )
	                .toMonth(
	                        education.getToDate() != null
	                                ? getMonthName(education.getToDate())
	                                : null
	                )
	                .toYear(
	                        education.getToDate() != null
	                                ? education.getToDate().getYear()
	                                : null
	                )

	                // Institute
	                .institutionName(education.getInstituteName())
	                .universityName(education.getUniversityName())

	                // Location
	                .city(education.getCity())
	                .state(education.getState())
	                .country(education.getCountry())

	                // Meta
	                .yearOfPassing(education.getYearOfPassing())
	                .typeOfEducation(education.getTypeOfEducation())

	                .build();
	    }


}
