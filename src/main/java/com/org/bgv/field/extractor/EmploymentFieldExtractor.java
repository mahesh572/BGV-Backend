package com.org.bgv.field.extractor;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.org.bgv.candidate.entity.WorkExperience;
import com.org.bgv.candidate.repository.WorkExperienceRepository;
import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.vendor.service.VerificationFieldExtractor;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmploymentFieldExtractor
        implements VerificationFieldExtractor {

    private final WorkExperienceRepository repository;
    
    @Override
    public CheckCategoryEnum getType() {
        return CheckCategoryEnum.WORK_EXPERIENCE;
    }


    @Override
    public Map<String, Object> extract(Long sourceId) {

        WorkExperience work =
                repository.findById(sourceId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Work experience record not found : " + sourceId));

        Map<String, Object> map = new LinkedHashMap<>();

        map.put(
                "companyName",
                work.getCompanyName());

        map.put(
                "position",
                work.getPosition());

        map.put(
                "employeeId",
                work.getEmployeeId());

        map.put(
                "startDate",
                work.getStartDate());

        map.put(
                "endDate",
                work.getEndDate());

        map.put(
                "currentlyWorking",
                work.getCurrentlyWorking());

        map.put(
                "employmentType",
                work.getEmploymentType());

        map.put(
                "noticePeriod",
                work.getNoticePeriod());

        map.put(
                "managerEmailId",
                work.getManagerEmailId());

        map.put(
                "hrEmailId",
                work.getHrEmailId());

        map.put(
                "address",
                work.getAddress());

        map.put(
                "city",
                work.getCity());

        map.put(
                "state",
                work.getState());

        map.put(
                "country",
                work.getCountry());

        map.put(
                "reason",
                work.getReason());

        return map;
    }
}