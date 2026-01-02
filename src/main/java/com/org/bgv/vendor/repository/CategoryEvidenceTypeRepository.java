package com.org.bgv.vendor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.CheckCategory;
import com.org.bgv.vendor.entity.CategoryEvidenceType;
import com.org.bgv.vendor.entity.EvidenceType;

@Repository
public interface CategoryEvidenceTypeRepository
extends JpaRepository<CategoryEvidenceType, Long> {

boolean existsByCategoryAndEvidenceType(CheckCategory category, EvidenceType evidenceType);

List<CategoryEvidenceType> findByCategoryCategoryIdAndActiveTrue(Long categoryId);

Optional<CategoryEvidenceType>
findByCategoryCategoryIdAndEvidenceTypeIdAndActiveTrue(
    Long categoryId,
    Long evidenceTypeId
);

}

