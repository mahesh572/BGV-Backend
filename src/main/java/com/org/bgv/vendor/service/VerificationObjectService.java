package com.org.bgv.vendor.service;

import org.springframework.stereotype.Service;

import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.entity.VerificationCaseCheck;
import com.org.bgv.enums.VerificationObjectStatus;
import com.org.bgv.vendor.entity.VerificationObject;
import com.org.bgv.vendor.repository.VerificationObjectRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VerificationObjectService {

    private final VerificationObjectRepository repository;
    private final FieldComparisonService fieldComparisonService;

    public VerificationObject create(
            VerificationCaseCheck check,
            CheckCategoryEnum type,
            Long sourceId,
            String objectName) {

        VerificationObject object =
                repository.save(
                        VerificationObject.builder()
                                .verificationCheck(check)
                                .objectType(type)
                                .sourceId(sourceId)
                                .objectName(objectName)
                                .status(VerificationObjectStatus.PENDING)
                                .candidateSubmitted(true)
                                .build());

        // populate comparison rows
        fieldComparisonService.createComparisonFields(object);

        return object;
    }
}
