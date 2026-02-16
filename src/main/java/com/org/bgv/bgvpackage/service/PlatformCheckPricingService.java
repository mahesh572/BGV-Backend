package com.org.bgv.bgvpackage.service;

import com.org.bgv.bgvpackage.dto.PlatformCheckPricingRequest;
import com.org.bgv.bgvpackage.dto.PlatformCheckPricingResponse;
import com.org.bgv.bgvpackage.entity.PlatformCheckPricing;
import com.org.bgv.bgvpackage.repository.PlatformCheckPricingRepository;
import com.org.bgv.common.CheckCategoryResponse;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.RuleTypes;
import com.org.bgv.enums.PricingType;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.repository.RuleTypesRepository;
import com.org.bgv.service.CheckCategoryService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlatformCheckPricingService {

    private final PlatformCheckPricingRepository pricingRepository;
    private final CheckCategoryRepository checkCategoryRepository;
    private final RuleTypesRepository ruleTypesRepository;
    private final CheckCategoryService checkCategoryService;

    // ===============================
    // CREATE
    // ===============================
    @Transactional
    public PlatformCheckPricing createPricing(
    		PlatformCheckPricingRequest platformCheckPricingRequest 
    ) {
    	
    	// CheckCategoryResponse category = checkCategoryService.getCheckCategoryById(platformCheckPricingRequest.getCheckCategoryId()).orElseGet(null);
    	
    	CheckCategory checkCategory=checkCategoryRepository.findById(platformCheckPricingRequest.getCheckCategoryId()).orElseGet(null);

    	 RuleTypes ruleType = ruleTypesRepository.findById(platformCheckPricingRequest.getRuleTypeId()).orElseGet(null);

    	 
    	 
        pricingRepository
                .findByCheckCategoryAndRuleTypeAndActiveTrue(checkCategory, ruleType)
                .ifPresent(existing -> {
                    throw new RuntimeException(
                            "Active pricing already exists for this category and rule"
                    );
                });

        PlatformCheckPricing pricing = new PlatformCheckPricing();
        pricing.setCheckCategory(checkCategory);
        pricing.setRuleType(ruleType);
        pricing.setPricingType(platformCheckPricingRequest.getPricingType());
        pricing.setUnitPrice(platformCheckPricingRequest.getUnitPrice());
        pricing.setActive(true);

        return pricingRepository.save(pricing);
    }

    // ===============================
    // UPDATE
    // ===============================
    @Transactional
    public PlatformCheckPricingResponse updatePricing(Long pricingId,
    		PlatformCheckPricingRequest platformCheckPricingRequest
    ) {

        PlatformCheckPricing pricing = pricingRepository.findById(pricingId)
                .orElseThrow(() ->
                        new RuntimeException("Pricing not found"));
        
        RuleTypes ruleType = ruleTypesRepository.findById(platformCheckPricingRequest.getRuleTypeId()).orElseGet(null);

        pricing.setPricingType(platformCheckPricingRequest.getPricingType());
        pricing.setUnitPrice(platformCheckPricingRequest.getUnitPrice());
        pricing.setRuleType(ruleType);
        
         pricing = pricingRepository.save(pricing);
        
      return  mapToResponse(pricing);

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

        List<PlatformCheckPricing> list =
                pricingRepository
                        .findByCheckCategory_CategoryIdAndActiveTrue(categoryId);

        return list.stream()
                .map(this::mapToResponse)
                .toList();
    }
    
    
    private PlatformCheckPricingResponse mapToResponse(PlatformCheckPricing pricing) {

        return PlatformCheckPricingResponse.builder()
                .id(pricing.getId())
                .checkCategoryId(pricing.getCheckCategory().getCategoryId())
                .checkCategoryName(pricing.getCheckCategory().getName())
                .ruleTypeId(pricing.getRuleType().getRuleTypeId())
                .ruleTypeName(pricing.getRuleType().getName())
                .pricingType(pricing.getPricingType())
                .unitPrice(pricing.getUnitPrice())
                .build();
    }


}
