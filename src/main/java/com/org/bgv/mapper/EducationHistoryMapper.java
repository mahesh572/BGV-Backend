package com.org.bgv.mapper;

import com.org.bgv.dto.EducationHistoryDTO;
import com.org.bgv.entity.EducationHistory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

@Component
public class EducationHistoryMapper {

    public EducationHistoryDTO toDto(EducationHistory entity) {
        if (entity == null) {
            return null;
        }

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

    public EducationHistory toEntity(EducationHistoryDTO dto) {
        if (dto == null) {
            return null;
        }

        return EducationHistory.builder()
                .id(dto.getId())
                .grade(dto.getGrade())
                .gpa(dto.getGpa())
                .institute_name(dto.getInstitutionName())
                .university_name(dto.getUniversityName())
                .city(dto.getCity())
                .state(dto.getState())
                .country(dto.getCountry())
                .yearOfPassing(dto.getYearOfPassing())
                .typeOfEducation(dto.getTypeOfEducation())
                .fromDate(parseDate(dto.getFromMonth(), dto.getFromYear()))
                .toDate(parseDate(dto.getToMonth(), dto.getToYear()))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void updateEntityFromDto(EducationHistoryDTO dto, EducationHistory entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setGrade(dto.getGrade());
        entity.setGpa(dto.getGpa());
        entity.setInstitute_name(dto.getInstitutionName());
        entity.setUniversity_name(dto.getUniversityName());
        entity.setCity(dto.getCity());
        entity.setState(dto.getState());
        entity.setCountry(dto.getCountry());
        entity.setYearOfPassing(dto.getYearOfPassing());
        entity.setTypeOfEducation(dto.getTypeOfEducation());
        entity.setFromDate(parseDate(dto.getFromMonth(), dto.getFromYear()));
        entity.setToDate(parseDate(dto.getToMonth(), dto.getToYear()));
        entity.setUpdatedAt(LocalDateTime.now());
    }

    private String getMonthName(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    private LocalDate parseDate(String monthName, Integer year) {
        if (monthName == null || year == null) {
            return null;
        }
        
        try {
            Month month = Month.valueOf(monthName.toUpperCase());
            return LocalDate.of(year, month, 1);
        } catch (IllegalArgumentException e) {
            // Handle invalid month names gracefully
            return LocalDate.of(year, 1, 1);
        }
    }
}