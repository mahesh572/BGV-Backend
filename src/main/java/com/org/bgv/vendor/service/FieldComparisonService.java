package com.org.bgv.vendor.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.org.bgv.common.entity.FieldConfiguration;
import com.org.bgv.common.repository.FieldConfigurationRepository;
import com.org.bgv.config.SecurityUtils;
import com.org.bgv.enums.ComparisonStatus;
import com.org.bgv.enums.VerificationObjectStatus;
import com.org.bgv.vendor.dto.UpdateFieldComparisonRequest;
import com.org.bgv.vendor.entity.VerificationFieldComparison;
import com.org.bgv.vendor.entity.VerificationObject;
import com.org.bgv.vendor.repository.VerificationFieldComparisonRepository;
import com.org.bgv.vendor.repository.VerificationObjectRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class FieldComparisonService {

    private final FieldConfigurationRepository fieldConfigurationRepository;

    private final VerificationFieldComparisonRepository comparisonRepository;

    private final List<VerificationFieldExtractor> extractorList;

    private Map<com.org.bgv.dto.CheckCategoryEnum,
            VerificationFieldExtractor> extractorMap;
    
    private final VerificationObjectRepository verificationObjectRepository;


    @PostConstruct
    public void init() {

        extractorMap =
                extractorList.stream()
                        .collect(
                                Collectors.toMap(
                                        VerificationFieldExtractor::getType,
                                        Function.identity()
                                )
                        );
    }
    
    
    @Transactional
    public void updateSourceValue(
            Long comparisonId,
            String sourceValue) {

        VerificationFieldComparison comparison =
        		comparisonRepository.findById(comparisonId)
                        .orElseThrow();

        comparison.setSourceValue(sourceValue);

        comparisonRepository.save(comparison);
        updateObjectStatus(
                comparison.getVerificationObject());
        
    }
    
    private ComparisonStatus compare(
            String candidateValue,
            String sourceValue) {

        if (sourceValue == null || sourceValue.isBlank()) {
            return ComparisonStatus.NOT_AVAILABLE;
        }

        return candidateValue != null &&
               candidateValue.equalsIgnoreCase(sourceValue)
                ? ComparisonStatus.MATCH
                : ComparisonStatus.MISMATCH;
    }
    
    
    private void updateObjectStatus(VerificationObject object) {

        log.info(
                "Recalculating status for objectId={}",
                object.getObjectId());

        List<VerificationFieldComparison> fields =
                comparisonRepository.findByVerificationObject(object);

        long mismatchCount =
                fields.stream()
                        .filter(f -> f.getResult() == ComparisonStatus.MISMATCH)
                        .count();

        long pendingCount =
                fields.stream()
                        .filter(f -> f.getResult() == ComparisonStatus.PENDING)
                        .count();

        log.info(
                "ObjectId={} : totalFields={}, mismatchCount={}, pendingCount={}",
                object.getObjectId(),
                fields.size(),
                mismatchCount,
                pendingCount);

        if (pendingCount > 0) {

            object.setStatus(VerificationObjectStatus.IN_PROGRESS);

        } else if (mismatchCount > 0) {

            object.setFinalResult(VerificationObjectStatus.DISCREPANCY_FOUND);
            object.setStatus(VerificationObjectStatus.COMPLETED);

        } else {

            object.setFinalResult(VerificationObjectStatus.CLEAR);
            object.setStatus(VerificationObjectStatus.COMPLETED);
            object.setVerifiedAt(LocalDateTime.now());
        }

        verificationObjectRepository.save(object);

        log.info(
                "Object saved. objectId={}, status={}, finalResult={}",
                object.getObjectId(),
                object.getStatus(),
                object.getFinalResult());
    }

    public void createComparisonFields(VerificationObject object) {

        // avoid duplicate rows
        if (comparisonRepository.existsByVerificationObject(object)) {
            return;
        }

        VerificationFieldExtractor extractor =
                extractorMap.get(object.getObjectType());

        if (extractor == null) {
            throw new RuntimeException(
                    "No field extractor found for "
                            + object.getObjectType());
        }

        Map<String, Object> values =
                extractor.extract(object.getSourceId());

        List<FieldConfiguration> configurations =
                fieldConfigurationRepository
                        .findByCheckTypeOrderBySequenceNo(
                                object.getObjectType());

        List<VerificationFieldComparison> comparisons =
                configurations.stream()
                        .filter(FieldConfiguration::getComparable)
                        .map(config -> {

                            Object candidateValue =
                                    values.get(config.getFieldName());

                            return VerificationFieldComparison
                                    .builder()
                                    .verificationObject(object)
                                    .fieldName(config.getFieldName())
                                    .displayName(config.getDisplayName())
                                    .candidateValue(
                                            candidateValue == null
                                                    ? null
                                                    : candidateValue.toString())
                                    .sourceValue(null)
                                    .result(ComparisonStatus.PENDING)
                                    .verified(false)
                                    .remarks(null)
                                    .build();
                        })
                        .toList();

        comparisonRepository.saveAll(comparisons);
    }
    
    
    @Transactional
    public void updateField(UpdateFieldComparisonRequest request) {

        log.info(
                "Updating comparison field. objectId={}, comparisonId={}, status={}, sourceValue={}",
                request.getObjectId(),
                request.getComparisonId(),
                request.getStatus(),
                request.getSourceValue());

        VerificationObject object =
                verificationObjectRepository.findBySourceId(request.getObjectId())
                        .orElseThrow(() -> {
                            log.error(
                                    "Verification object not found for sourceId={}",
                                    request.getObjectId());
                            return new RuntimeException("Verification object not found");
                        });

        log.info(
                "Verification object found. objectId={}, sourceId={}, status={}",
                object.getObjectId(),
                object.getSourceId(),
                object.getStatus());

        VerificationFieldComparison comparison =
                comparisonRepository.findById(request.getComparisonId())
                        .orElseThrow(() -> {
                            log.error(
                                    "Comparison field not found. comparisonId={}",
                                    request.getComparisonId());
                            return new RuntimeException("Comparison field not found");
                        });

        log.info(
                "Comparison found. comparisonId={}, fieldName={}, currentStatus={}, candidateValue={}, sourceValue={}",
                comparison.getId(),
                comparison.getFieldName(),
                comparison.getResult(),
                comparison.getCandidateValue(),
                comparison.getSourceValue());

        // Safety check
        if (!comparison.getVerificationObject()
                .getObjectId()
                .equals(object.getObjectId())) {

            log.error(
                    "Field does not belong to verification object. comparisonObjectId={}, requestObjectId={}",
                    comparison.getVerificationObject().getObjectId(),
                    object.getObjectId());

            throw new RuntimeException(
                    "Field does not belong to verification object");
        }

        log.info(
                "Updating comparison field. Old status={}, New status={}, Old sourceValue={}, New sourceValue={}",
                comparison.getResult(),
                request.getStatus(),
                comparison.getSourceValue(),
                request.getSourceValue());

        comparison.setSourceValue(request.getSourceValue());
        comparison.setResult(request.getStatus());
        comparison.setRemarks(request.getRemarks());
        comparison.setVerified(true);
        comparison.setVerifiedAt(LocalDateTime.now());
        comparison.setVerifiedBy(SecurityUtils.getCurrentUserId());
        

        comparisonRepository.save(comparison);

        log.info(
                "Comparison updated successfully. comparisonId={}",
                comparison.getId());

        // recalculate object status
        updateObjectStatus(object);

        log.info(
                "Verification object status recalculated. objectId={}, status={}, finalResult={}",
                object.getObjectId(),
                object.getStatus(),
                object.getFinalResult());
    }
}