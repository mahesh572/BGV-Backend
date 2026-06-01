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

        VerificationMethod documentReview =
                saveMethod("DOCUMENT_REVIEW",
                        "Document Review");

        VerificationMethod email =
                saveMethod("EMAIL",
                        "Email Verification");

        VerificationMethod phone =
                saveMethod("PHONE",
                        "Phone Verification");

        VerificationMethod portal =
                saveMethod("PORTAL",
                        "Portal Verification");

        VerificationMethod physicalVisit =
                saveMethod("PHYSICAL_VISIT",
                        "Physical Visit");

        VerificationMethod referenceCall =
                saveMethod("REFERENCE_CALL",
                        "Reference Call");

        VerificationMethod databaseCheck =
                saveMethod("DATABASE_CHECK",
                        "Database Check");

        VerificationMethod videoVerification =
                saveMethod("VIDEO_VERIFICATION",
                        "Video Verification");

        VerificationMethod employerHr =
                saveMethod("EMPLOYER_HR_CONFIRMATION",
                        "Employer HR Confirmation");

        VerificationMethod managerConfirmation =
                saveMethod("MANAGER_CONFIRMATION",
                        "Manager Confirmation");

        /*
         * WORK
         */
        map(CheckCategoryEnum.WORK, documentReview);
        map(CheckCategoryEnum.WORK, email);
        map(CheckCategoryEnum.WORK, phone);
        map(CheckCategoryEnum.WORK, employerHr);
        map(CheckCategoryEnum.WORK, managerConfirmation);

        /*
         * EDUCATION
         */
        map(CheckCategoryEnum.EDUCATION, documentReview);
        map(CheckCategoryEnum.EDUCATION, email);
        map(CheckCategoryEnum.EDUCATION, portal);
        map(CheckCategoryEnum.EDUCATION, phone);

        /*
         * ADDRESS
         */
        map(CheckCategoryEnum.ADDRESS, documentReview);
        map(CheckCategoryEnum.ADDRESS, physicalVisit);
        map(CheckCategoryEnum.ADDRESS, phone);

        /*
         * IDENTITY
         */
        map(CheckCategoryEnum.IDENTITY, documentReview);
        map(CheckCategoryEnum.IDENTITY, databaseCheck);
        map(CheckCategoryEnum.IDENTITY, videoVerification);

        /*
         * REFERENCE
         */
        map(CheckCategoryEnum.REFERENCE, phone);
        map(CheckCategoryEnum.REFERENCE, email);
        map(CheckCategoryEnum.REFERENCE, referenceCall);

        /*
         * CRIMINAL
         */
       // map(CheckCategoryEnum.CRIMINAL, databaseCheck);
       // map(CheckCategoryEnum.CRIMINAL, documentReview);

        /*
         * CREDIT
         */
        map(CheckCategoryEnum.CREDIT, databaseCheck);

        /*
         * DATABASE
         */
        map(CheckCategoryEnum.DATABASE, databaseCheck);
    }

    private VerificationMethod saveMethod(
            String code,
            String name) {

        VerificationMethod method =
                new VerificationMethod();

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
