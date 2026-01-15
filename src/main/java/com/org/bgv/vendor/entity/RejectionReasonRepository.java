package com.org.bgv.vendor.entity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.entity.CheckCategory;

public interface RejectionReasonRepository
extends JpaRepository<ActionReason, Long> {

List<ActionReason> findByLevelAndActiveTrueOrderBySortOrder(
		ActionReason level
);

List<ActionReason> findByCategory_CategoryIdAndLevelAndActiveTrueOrderBySortOrder(
    Long categoryId,
    ActionReason level
);

List<ActionReason> findByCategoryAndLevelAndActiveTrueOrderBySortOrder(
        CheckCategory category,
        ActionReason level
);

}