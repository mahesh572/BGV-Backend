package com.org.bgv.vendor.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.org.bgv.entity.CheckCategory;
import com.org.bgv.vendor.entity.CategoryActionReason;

public interface CategoryActionReasonRepository
        extends JpaRepository<CategoryActionReason, Long> {

    // ------------------------------------------------
    // 🔹 Basic Lookups
    // ------------------------------------------------
	
	boolean existsByCategoryAndReasonCode(CheckCategory category, String reasonCode);

    List<CategoryActionReason> findByCategory_CategoryIdAndActiveTrue(Long categoryId);

    List<CategoryActionReason> findByCategory_CategoryId(Long categoryId);

    Optional<CategoryActionReason> findByCategory_CategoryIdAndReason_ActionReasonId(
            Long categoryId,
            Long reasonId
    );

    // ------------------------------------------------
    // 🔹 Vendor UI – Load allowed actions for category
    // ------------------------------------------------

    @Query("""
        SELECT car
        FROM CategoryActionReason car
        JOIN FETCH car.reason r
        WHERE car.category.categoryId = :categoryId
          AND car.active = true
          AND r.active = true
        ORDER BY r.sortOrder ASC
    """)
    List<CategoryActionReason> findActiveReasonsByCategory(
            @Param("categoryId") Long categoryId
    );

    // ------------------------------------------------
    // 🔹 Validation
    // ------------------------------------------------

    boolean existsByCategory_CategoryIdAndReason_ActionReasonId(
            Long categoryId,
            Long reasonId
    );

    // ------------------------------------------------
    // 🔹 Admin / Maintenance
    // ------------------------------------------------

    void deleteByCategory_CategoryId(Long categoryId);
}

