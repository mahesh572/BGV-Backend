package com.org.bgv.field.extractor;

import org.springframework.stereotype.Service;

import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.vendor.service.VerificationFieldExtractor;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FieldExtractorFactory {

    private final EducationFieldExtractor educationExtractor;
    private final EmploymentFieldExtractor employmentExtractor;

    public VerificationFieldExtractor getExtractor(
            CheckCategoryEnum type) {

        return switch (type) {

            case EDUCATION -> educationExtractor;

            case WORK_EXPERIENCE -> employmentExtractor;

            default -> throw new RuntimeException(
                    "No extractor found");
        };
    }
}
