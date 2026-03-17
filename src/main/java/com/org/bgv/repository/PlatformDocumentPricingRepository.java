package com.org.bgv.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.entity.PlatformDocumentPricing;

public interface PlatformDocumentPricingRepository 
extends JpaRepository<PlatformDocumentPricing, Long> {

List<PlatformDocumentPricing> 
findByCheckCategory_CategoryIdAndActiveTrue(Long categoryId);

Optional<PlatformDocumentPricing> 
findByCheckCategory_CategoryIdAndDocumentType_DocTypeIdAndActiveTrue(
        Long categoryId,
        Long documentTypeId
);
}