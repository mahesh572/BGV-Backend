package com.org.bgv.field.extractor;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.org.bgv.candidate.entity.EducationHistory;
import com.org.bgv.candidate.repository.EducationHistoryRepository;
import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.vendor.service.VerificationFieldExtractor;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EducationFieldExtractor
        implements VerificationFieldExtractor {

    private final EducationHistoryRepository repository;
    
    @Override
    public CheckCategoryEnum getType() {
        return CheckCategoryEnum.EDUCATION;
    }

    @Override
    public Map<String, Object> extract(Long sourceId) {

        EducationHistory education =
                repository.findById(sourceId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Education record not found : " + sourceId));

        Map<String, Object> map = new LinkedHashMap<>();

        map.put(
                "instituteName",
                education.getInstituteName());

        map.put(
                "universityName",
                education.getUniversityName());

        map.put(
                "degree",
                education.getDegree() != null
                        ? education.getDegree().getName()
                        : null);

        map.put(
                "field",
                education.getField() != null
                        ? education.getField().getName()
                        : null);

        map.put(
                "fromDate",
                education.getFromDate());

        map.put(
                "toDate",
                education.getToDate());

        map.put(
                "yearOfPassing",
                education.getYearOfPassing());

        map.put(
                "grade",
                education.getGrade());

        map.put(
                "gpa",
                education.getGpa());

        map.put(
                "city",
                education.getCity());

        map.put(
                "state",
                education.getState());

        map.put(
                "country",
                education.getCountry());

        map.put(
                "typeOfEducation",
                education.getTypeOfEducation());

        return map;
    }
}