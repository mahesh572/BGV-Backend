package com.org.bgv.bgvpackage.service;

import com.org.bgv.bgvpackage.dto.PlatformCheckPricingRequest;
import com.org.bgv.bgvpackage.dto.PlatformCheckPricingResponse;
import com.org.bgv.bgvpackage.entity.PlatformCheckPricing;
import com.org.bgv.bgvpackage.repository.PlatformCheckPricingRepository;
import com.org.bgv.common.CheckCategoryResponse;
import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.DocumentType;
import com.org.bgv.entity.PlatformDocumentPricing;
import com.org.bgv.entity.RuleTypes;
import com.org.bgv.enums.PricingLevel;
import com.org.bgv.enums.PricingType;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.repository.DocumentTypeRepository;
import com.org.bgv.repository.PlatformDocumentPricingRepository;
import com.org.bgv.repository.RuleTypesRepository;
import com.org.bgv.service.CheckCategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlatformCheckPricingService {

    private final PlatformCheckPricingRepository pricingRepository;
    private final CheckCategoryRepository checkCategoryRepository;
    private final RuleTypesRepository ruleTypesRepository;
    private final CheckCategoryService checkCategoryService;
    private final DocumentTypeRepository documentTypeRepository;
    private final PlatformDocumentPricingRepository platformDocumentPricingRepository;

    // ===============================
    // CREATE
    // ===============================
    @Transactional
    public void createPricing(PlatformCheckPricingRequest request) {

        log.info("PlatformCheckPricingService :: createPricing :: {}", request);

        // 1️⃣ Fetch Category
        CheckCategory checkCategory = checkCategoryRepository.findById(
                request.getCheckCategoryId()
        ).orElseThrow(() -> new RuntimeException("Invalid category id"));

        // =========================================================
        // 🔹 DOCUMENT LEVEL PRICING
        // =========================================================
        if (PricingLevel.DOCUMENT.equals(request.getLevel())) {

            DocumentType documentType = documentTypeRepository
                    .findById(request.getDocumentTypeId())
                    .orElseThrow(() -> new RuntimeException("Invalid document type id"));

            // 🔥 Prevent duplicate document pricing
            platformDocumentPricingRepository
                    .findByCheckCategory_CategoryIdAndDocumentType_DocTypeIdAndActiveTrue(
                            checkCategory.getCategoryId(),
                            documentType.getDocTypeId()
                    )
                    .ifPresent(existing -> {
                        throw new RuntimeException(
                                "Active pricing already exists for this category and document"
                        );
                    });

            PlatformDocumentPricing documentPricing =
                    PlatformDocumentPricing.builder()
                            .checkCategory(checkCategory)
                            .documentType(documentType)
                            .pricingType(request.getPricingType())
                            .unitPrice(request.getUnitPrice())
                           // .minCharge(request.getMinCharge())
                           // .maxCharge(request.getMaxCharge())
                            .active(true)
                            .build();

            platformDocumentPricingRepository.save(documentPricing);

            return;
        }

        // =========================================================
        // 🔹 RULE LEVEL PRICING
        // =========================================================

        RuleTypes ruleType = ruleTypesRepository.findById(
                request.getRuleTypeId()
        ).orElseThrow(() -> new RuntimeException("Invalid rule type id"));

        // 🔥 Prevent duplicate rule pricing
        pricingRepository
                .findByCheckCategoryAndRuleTypeAndActiveTrue(
                        checkCategory, ruleType
                )
                .ifPresent(existing -> {
                    throw new RuntimeException(
                            "Active pricing already exists for this category and rule"
                    );
                });

        PlatformCheckPricing pricing = new PlatformCheckPricing();
        pricing.setCheckCategory(checkCategory);
        pricing.setRuleType(ruleType);
        pricing.setPricingType(request.getPricingType());
        pricing.setUnitPrice(request.getUnitPrice());
        pricing.setActive(true);

        pricingRepository.save(pricing);
    }

    // ===============================
    // UPDATE
    // ===============================
    @Transactional
    public void updatePricing(Long pricingId,
    		PlatformCheckPricingRequest platformCheckPricingRequest
    ) {

    	if(platformCheckPricingRequest.getLevel()!=null && platformCheckPricingRequest.getLevel().equals(PricingLevel.DOCUMENT)) {
    		
    		PlatformDocumentPricing platformDocumentPricing = platformDocumentPricingRepository.findById(platformCheckPricingRequest.getPricingId()).orElseGet(null);
    		platformDocumentPricing.setUnitPrice(platformCheckPricingRequest.getUnitPrice());
    		platformDocumentPricing.setPricingType(platformCheckPricingRequest.getPricingType());
    		platformDocumentPricingRepository.save(platformDocumentPricing);
    	
    	}else {
    	
    	
        PlatformCheckPricing pricing = pricingRepository.findById(pricingId)
                .orElseThrow(() ->
                        new RuntimeException("Pricing not found"));
        
        RuleTypes ruleType = ruleTypesRepository.findById(platformCheckPricingRequest.getRuleTypeId()).orElseGet(null);

        pricing.setPricingType(platformCheckPricingRequest.getPricingType());
        pricing.setUnitPrice(platformCheckPricingRequest.getUnitPrice());
        pricing.setRuleType(ruleType);
        
         pricing = pricingRepository.save(pricing);
    	}
        
    //  return  mapToResponse(pricing);

      //  return pricingRepository.save(pricing);
    }

    // ===============================
    // SOFT DELETE
    // ===============================
    @Transactional
    public void deactivatePricing(Long pricingId) {

        PlatformCheckPricing pricing = pricingRepository.findById(pricingId)
                .orElseThrow(() ->
                        new RuntimeException("Pricing not found"));

        pricing.setActive(false);

        pricingRepository.save(pricing);
    }

    // ===============================
    // HARD DELETE (Not recommended)
    // ===============================
    @Transactional
    public void deletePricing(Long pricingId) {
        pricingRepository.deleteById(pricingId);
    }
    
    
    @Transactional(readOnly = true)
    public List<PlatformCheckPricingResponse> getPricingByCategoryId(Long categoryId) {

        CheckCategory checkCategory = checkCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // 🔹 If Identity → DOCUMENT pricing
        if (CheckCategoryEnum.IDENTITY.name()
                .equalsIgnoreCase(checkCategory.getName())) {

            return platformDocumentPricingRepository
                    .findByCheckCategory_CategoryIdAndActiveTrue(categoryId)
                    .stream()
                    .map(this::mapDocumentPricingToResponse)
                    .toList();
        }

        // 🔹 Other categories → RULE pricing
        return pricingRepository
                .findByCheckCategory_CategoryIdAndActiveTrue(categoryId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    
    private PlatformCheckPricingResponse mapDocumentPricingToResponse(
            PlatformDocumentPricing pricing) {

        DocumentType doc = pricing.getDocumentType();
        CheckCategory category = pricing.getCheckCategory();

        return PlatformCheckPricingResponse.builder()
                .id(pricing.getId())
                .pricingId(pricing.getId())
                .checkCategoryId(category.getCategoryId())
                .checkCategoryName(category.getName())
                .ruleTypeId(doc.getDocTypeId()) // document id
                .ruleTypeName(doc.getName())
                .level("DOCUMENT")
                .pricingType(pricing.getPricingType())
                .unitPrice(pricing.getUnitPrice())
                .documentLabel(doc.getName())
                .build();
    }
    
    
    private PlatformCheckPricingResponse mapToResponse(PlatformCheckPricing pricing) {

        return PlatformCheckPricingResponse.builder()
                .id(pricing.getId())
                .pricingId(pricing.getId())
                .checkCategoryId(pricing.getCheckCategory().getCategoryId())
                .checkCategoryName(pricing.getCheckCategory().getName())
                .ruleTypeId(pricing.getRuleType().getRuleTypeId())
                .ruleTypeName(pricing.getRuleType().getName())
                .pricingType(pricing.getPricingType())
                .unitPrice(pricing.getUnitPrice())
                .build();
    }
    
    /*
    private PlatformCheckPricingResponse mapDocumentToResponse(DocumentType doc) {
    	
    	List<PlatformDocumentPricing>  platOptional = platformDocumentPricingRepository.findByCheckCategory_CategoryIdAndActiveTrue(doc.getCategory().getCategoryId());
    	
    	// PlatformDocumentPricing pricing = platOptional.orElse(null);
    	
    	if(platOptional==null || platOptional.size()==0) {
    		return null;
    	}

        return PlatformCheckPricingResponse.builder()
                .id(doc.getDocTypeId())
                .checkCategoryId(doc.getCategory().getCategoryId())
                .checkCategoryName(doc.getCategory().getName())
                .ruleTypeId(doc.getDocTypeId()) // Not applicable
                .ruleTypeName(doc.getName())
                .level("DOCUMENT")
                .pricingType(PricingType.FLAT)
                .unitPrice(pricing != null ? pricing.getUnitPrice() : null)
                .documentLabel(doc.getName())
                .build();
    }
    */
    /*
    private PlatformCheckPricingResponse mapDocumentToResponse(
            PlatformCheckPricing pricing) {

        return PlatformCheckPricingResponse.builder()
                .id(pricing.getId())
                .checkCategoryId(pricing.getCheckCategory().getCategoryId())
                .checkCategoryName(pricing.getCheckCategory().getName())
                .ruleTypeId(pricing.getRuleType().getRuleTypeId())
                .ruleTypeName(pricing.getRuleType().getName())
                .ruleTypeCode(pricing.getRuleType().getCode())
                .level("DOCUMENT")
                .pricingType(pricing.getPricingType())
                .unitPrice(pricing.getUnitPrice())
               // .minCharge(pricing.getMinCharge())
               // .maxCharge(pricing.getMaxCharge())
                .active(pricing.getActive())
                .build();
    }
*/
}
