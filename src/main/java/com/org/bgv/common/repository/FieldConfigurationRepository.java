package com.org.bgv.common.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.common.entity.FieldConfiguration;
import com.org.bgv.dto.CheckCategoryEnum;

@Repository
public interface FieldConfigurationRepository
        extends JpaRepository<FieldConfiguration, Long> {
	
	List<FieldConfiguration>
	findByCheckTypeOrderBySequenceNo(CheckCategoryEnum checkType);

    /**
     * Get all fields for a check type ordered by UI sequence
     */
    List<FieldConfiguration> findByCheckTypeOrderBySequenceNoAsc(
            CheckCategoryEnum checkType);

    /**
     * Only visible fields
     */
    List<FieldConfiguration> findByCheckTypeAndVisibleTrueOrderBySequenceNoAsc(
            CheckCategoryEnum checkType);

    /**
     * Fields that participate in comparison
     */
    List<FieldConfiguration> findByCheckTypeAndComparableTrueOrderBySequenceNoAsc(
            CheckCategoryEnum checkType);

    /**
     * Fields shown in report
     */
    List<FieldConfiguration> findByCheckTypeAndReportableTrueOrderBySequenceNoAsc(
            CheckCategoryEnum checkType);

    /**
     * Find single field metadata
     */
    FieldConfiguration findByCheckTypeAndFieldName(
            CheckCategoryEnum checkType,
            String fieldName);

    /**
     * Check existence
     */
    boolean existsByCheckTypeAndFieldName(
            CheckCategoryEnum checkType,
            String fieldName);
}