package com.org.bgv.vendor.entity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.entity.CheckCategory;
import com.org.bgv.vendor.dto.RejectionLevel;

public interface RejectionReasonRepository
extends JpaRepository<RejectionReason, Long> {

List<RejectionReason> findByLevelAndActiveTrueOrderBySortOrder(
    RejectionLevel level
);

List<RejectionReason> findByCategory_CategoryIdAndLevelAndActiveTrueOrderBySortOrder(
    Long categoryId,
    RejectionLevel level
);

List<RejectionReason> findByCategoryAndLevelAndActiveTrueOrderBySortOrder(
        CheckCategory category,
        RejectionLevel level
);

}