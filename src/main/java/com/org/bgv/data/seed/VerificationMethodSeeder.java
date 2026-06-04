package com.org.bgv.data.seed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.vendor.entity.CheckVerificationMethod;
import com.org.bgv.vendor.entity.VerificationMethod;
import com.org.bgv.vendor.repository.CheckVerificationMethodRepository;
import com.org.bgv.vendor.repository.VerificationMethodRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VerificationMethodSeeder implements CommandLineRunner {

    private final VerificationMethodRepository methodRepository;
    private final CheckVerificationMethodRepository mappingRepository;

    @Override
    @Transactional
    public void run(String... args) {

        if (methodRepository.count() > 0) {
            return;
        }

        VerificationMethod email =
                saveMethod("EMAIL", "Email");

        VerificationMethod phone =
                saveMethod("PHONE", "Phone Call");

        VerificationMethod portal =
                saveMethod("PORTAL", "Portal / System");

        VerificationMethod physicalVisit =
                saveMethod("PHYSICAL_VISIT", "Physical Visit");

        VerificationMethod database =
                saveMethod("DATABASE", "Database Check");

        VerificationMethod videoCall =
                saveMethod("VIDEO_CALL", "Video Verification");

        VerificationMethod documentReview =
                saveMethod("DOCUMENT_REVIEW", "Document Review");

        VerificationMethod other =
                saveMethod("OTHER", "Other");

        /*
         * WORK
         */
        map(CheckCategoryEnum.WORK_EXPERIENCE, documentReview);
        map(CheckCategoryEnum.WORK_EXPERIENCE, email);
        map(CheckCategoryEnum.WORK_EXPERIENCE, phone);
        map(CheckCategoryEnum.WORK_EXPERIENCE, portal);
        map(CheckCategoryEnum.WORK_EXPERIENCE, physicalVisit);

        /*
         * EDUCATION
         */
        map(CheckCategoryEnum.EDUCATION, documentReview);
        map(CheckCategoryEnum.EDUCATION, email);
        map(CheckCategoryEnum.EDUCATION, phone);
        map(CheckCategoryEnum.EDUCATION, portal);

        /*
         * ADDRESS
         */
        map(CheckCategoryEnum.ADDRESS, documentReview);
        map(CheckCategoryEnum.ADDRESS, phone);
        map(CheckCategoryEnum.ADDRESS, physicalVisit);

        /*
         * IDENTITY
         */
        map(CheckCategoryEnum.IDENTITY, documentReview);
        map(CheckCategoryEnum.IDENTITY, database);
        map(CheckCategoryEnum.IDENTITY, videoCall);

        /*
         * REFERENCE
         */
        map(CheckCategoryEnum.REFERENCE, phone);
        map(CheckCategoryEnum.REFERENCE, email);

        /*
         * CRIMINAL
         */
        // map(CheckCategoryEnum.CRIMINAL, database);
        // map(CheckCategoryEnum.CRIMINAL, portal);

        /*
         * CREDIT
         */
        map(CheckCategoryEnum.CREDIT, database);

        /*
         * DATABASE
         */
        map(CheckCategoryEnum.DATABASE, database);
    }

    private VerificationMethod saveMethod(
            String code,
            String name) {

        VerificationMethod method = new VerificationMethod();

        method.setCode(code);
        method.setName(name);
        method.setActive(true);

        return methodRepository.save(method);
    }

    private void map(
            CheckCategoryEnum checkType,
            VerificationMethod method) {

        CheckVerificationMethod mapping =
                new CheckVerificationMethod();

        mapping.setCheckType(checkType);
        mapping.setVerificationMethod(method);

        mappingRepository.save(mapping);
    }
}
