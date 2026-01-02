package com.org.bgv.vendor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.vendor.entity.EvidenceType;

public interface EvidenceTypeRepository extends JpaRepository<EvidenceType, Long> {

    Optional<EvidenceType> findByCode(String code);
    
 //   List<EvidenceType> findByCategories_CategoryId(Long categoryId);
}
