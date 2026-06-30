package com.org.bgv.data.seed;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.enums.VerificationMethodCode;
import com.org.bgv.vendor.entity.CheckVerificationMethod;
import com.org.bgv.vendor.entity.VerificationMethod;
import com.org.bgv.vendor.repository.CheckVerificationMethodRepository;
import com.org.bgv.vendor.repository.VerificationMethodRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VerificationMethodSeeder implements CommandLineRunner {
	
	 @Value("${app.seed.enabled:false}")
	    private boolean seedEnabled;

    private final VerificationMethodRepository methodRepository;
    private final CheckVerificationMethodRepository mappingRepository;

    @Override
    @Transactional
    public void run(String... args) {
    	
    	if (!seedEnabled) {
            System.out.println("Database seeding is disabled.");
            return;
        }

        VerificationMethod email = saveOrUpdateMethod(
                VerificationMethodCode.EMAIL,
                "Email Verification",
                "Verification through email",
                3,
                24,
                true);

        VerificationMethod phone = saveOrUpdateMethod(
                VerificationMethodCode.PHONE_VERIFICATION,
                "Phone Verification",
                "Verification through phone call",
                3,
                2,
                true);

        VerificationMethod portal = saveOrUpdateMethod(
                VerificationMethodCode.PORTAL,
                "Portal Verification",
                "Verification through employer portal",
                2,
                24,
                true);

        VerificationMethod fieldVisit = saveOrUpdateMethod(
                VerificationMethodCode.FIELD_VISIT,
                "Field Visit",
                "Physical address verification",
                2,
                48,
                true);

        VerificationMethod database = saveOrUpdateMethod(
                VerificationMethodCode.DATABASE,
                "Database Check",
                "Verification using government or third-party databases",
                1,
                0,
                false);

        VerificationMethod videoCall = saveOrUpdateMethod(
                VerificationMethodCode.VIDEO_CALL,
                "Video Verification",
                "Video verification",
                2,
                4,
                true);

        VerificationMethod other = saveOrUpdateMethod(
                VerificationMethodCode.OTHER,
                "Other",
                "Other verification method",
                1,
                0,
                false);

        // mappings (idempotent)
        mapIfNotExists(CheckCategoryEnum.WORK_EXPERIENCE, email);
        mapIfNotExists(CheckCategoryEnum.WORK_EXPERIENCE, phone);
        mapIfNotExists(CheckCategoryEnum.WORK_EXPERIENCE, portal);
        mapIfNotExists(CheckCategoryEnum.WORK_EXPERIENCE, fieldVisit);

        mapIfNotExists(CheckCategoryEnum.EDUCATION, email);
        mapIfNotExists(CheckCategoryEnum.EDUCATION, phone);
        mapIfNotExists(CheckCategoryEnum.EDUCATION, portal);

        mapIfNotExists(CheckCategoryEnum.ADDRESS, phone);
        mapIfNotExists(CheckCategoryEnum.ADDRESS, fieldVisit);

        mapIfNotExists(CheckCategoryEnum.IDENTITY, database);
        mapIfNotExists(CheckCategoryEnum.IDENTITY, videoCall);

        mapIfNotExists(CheckCategoryEnum.REFERENCE, phone);
        mapIfNotExists(CheckCategoryEnum.REFERENCE, email);

        mapIfNotExists(CheckCategoryEnum.CREDIT, database);

        mapIfNotExists(CheckCategoryEnum.DATABASE, database);
    }

    private VerificationMethod saveOrUpdateMethod(
            VerificationMethodCode code,
            String name,
            String description,
            Integer maxAttempts,
            Integer retryIntervalHours,
            Boolean retryAllowed) {

        VerificationMethod method = methodRepository.findByCode(code)
                .orElse(new VerificationMethod());

        method.setCode(code);
        method.setName(name);
        method.setDescription(description);
        method.setMaxAttempts(maxAttempts);
        method.setRetryIntervalHours(retryIntervalHours);
        method.setRetryAllowed(retryAllowed);
        method.setActive(true);

        return methodRepository.save(method);
    }

    private void mapIfNotExists(CheckCategoryEnum checkType,
                                VerificationMethod method) {

        boolean exists = mappingRepository
                .existsByCheckTypeAndVerificationMethod(checkType, method);

        if (exists) return;

        CheckVerificationMethod mapping = new CheckVerificationMethod();
        mapping.setCheckType(checkType);
        mapping.setVerificationMethod(method);

        mappingRepository.save(mapping);
    }
}