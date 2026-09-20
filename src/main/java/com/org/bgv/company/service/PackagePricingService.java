package com.org.bgv.company.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.org.bgv.bgvpackage.entity.EmployerCheckPricing;
import com.org.bgv.bgvpackage.entity.PlatformCheckPricing;
import com.org.bgv.bgvpackage.repository.EmployerCheckPricingRepository;
import com.org.bgv.bgvpackage.repository.PlatformCheckPricingRepository;
import com.org.bgv.company.dto.PricingInfo;
import com.org.bgv.company.entity.EmployerDocumentPricing;
import com.org.bgv.company.repository.EmployerDocumentPricingRepository;
import com.org.bgv.entity.PlatformDocumentPricing;
import com.org.bgv.entity.RuleTypes;
import com.org.bgv.enums.RuleGroup;
import com.org.bgv.repository.PlatformDocumentPricingRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class PackagePricingService {

	private final PlatformDocumentPricingRepository platformDocumentPricingRepository;
	private final EmployerDocumentPricingRepository employerDocumentPricingRepository;
	private final PlatformCheckPricingRepository platformCheckPricingRepository;
	private final EmployerCheckPricingRepository employerCheckPricingRepository;

	
	public PricingInfo resolvePricing(
	        Long companyId,
	        Long categoryId,
	        RuleTypes ruleType,
	        boolean isEmployerCompany) {

	    if (ruleType == null) {
	        throw new IllegalArgumentException("RuleType must not be null");
	    }

	    log.debug(
	            "Resolving pricing → companyId={}, categoryId={}, ruleTypeId={}, ruleCode={}, ruleGroup={}, isEmployerCompany={}",
	            companyId,
	            categoryId,
	            ruleType.getRuleTypeId(),
	            ruleType.getCode(),
	            ruleType.getRuleGroup(),
	            isEmployerCompany);


	    /*
	     * ================================================================
	     * 1. DOCUMENT SELECTION
	     * ================================================================
	     *
	     * DOCUMENT_SELECTION pricing is based on DocumentType.
	     *
	     * Employer:
	     *      EmployerDocumentPricing
	     *          ↓ fallback
	     *      PlatformDocumentPricing
	     *
	     * Self registered / GLOBAL:
	     *      PlatformDocumentPricing
	     */
	    if (RuleGroup.DOCUMENT_SELECTION.equals(ruleType.getRuleGroup())) {

	        Long documentTypeId = ruleType.getDocumentTypeId();

	        if (documentTypeId == null) {

	            log.warn(
	                    "DocumentTypeId is NULL for ruleTypeId={}",
	                    ruleType.getRuleTypeId());

	            return PricingInfo.builder()
	                    .build();
	        }


	        /*
	         * ------------------------------------------------------------
	         * Employer-specific document pricing
	         * ------------------------------------------------------------
	         */
	        if (isEmployerCompany && companyId != null) {

	            Optional<EmployerDocumentPricing> employerPricing =
	                    employerDocumentPricingRepository
	                            .findByCompany_IdAndCheckCategory_CategoryIdAndDocumentType_docTypeId(
	                                    companyId,
	                                    categoryId,
	                                    documentTypeId);

	            if (employerPricing.isPresent()) {

	                EmployerDocumentPricing pricing =
	                        employerPricing.get();

	                log.debug(
	                        "Employer document pricing found → pricingType={}, unitPrice={}",
	                        pricing.getPricingType(),
	                        pricing.getUnitPrice());

	                return PricingInfo.builder()
	                        .pricingType(pricing.getPricingType())
	                        .unitPrice(pricing.getUnitPrice())
	                        .minCharge(pricing.getMinCharge())
	                        .maxCharge(pricing.getMaxCharge())
	                        .source("EMPLOYER")
	                        .build();
	            }

	            log.debug(
	                    "Employer document pricing not found → fallback to platform");
	        }


	        /*
	         * ------------------------------------------------------------
	         * Platform document pricing
	         * ------------------------------------------------------------
	         */
	        Optional<PlatformDocumentPricing> platformPricing =
	                platformDocumentPricingRepository
	                        .findByCheckCategory_CategoryIdAndDocumentType_DocTypeIdAndActiveTrue(
	                                categoryId,
	                                documentTypeId);

	        if (platformPricing.isPresent()) {

	            PlatformDocumentPricing pricing =
	                    platformPricing.get();

	            log.debug(
	                    "Platform document pricing found → pricingType={}, unitPrice={}",
	                    pricing.getPricingType(),
	                    pricing.getUnitPrice());

	            return PricingInfo.builder()
	                    .pricingType(pricing.getPricingType())
	                    .unitPrice(pricing.getUnitPrice())
	                    .source("PLATFORM")
	                    .build();
	        }

	        log.warn(
	                "No document pricing found → categoryId={}, documentTypeId={}",
	                categoryId,
	                documentTypeId);

	        return PricingInfo.builder()
	                .source("DEFAULT")
	                .unitPrice(BigDecimal.ZERO)
	                .build();
	    }


	    /*
	     * ================================================================
	     * 2. NON-DOCUMENT / CHECK RULE
	     * ================================================================
	     *
	     * Employer:
	     *      EmployerCheckPricing
	     *          ↓ fallback
	     *      PlatformCheckPricing
	     *
	     * Self registered / GLOBAL:
	     *      PlatformCheckPricing
	     */
	    if (isEmployerCompany && companyId != null) {

	        Optional<EmployerCheckPricing> employerPricing =
	                employerCheckPricingRepository
	                        .findByCompany_IdAndCheckCategory_CategoryIdAndRuleType_RuleTypeIdAndActiveTrue(
	                                companyId,
	                                categoryId,
	                                ruleType.getRuleTypeId());

	        if (employerPricing.isPresent()) {

	            EmployerCheckPricing pricing =
	                    employerPricing.get();

	            log.debug(
	                    "Employer check pricing found → pricingType={}, unitPrice={}",
	                    pricing.getPricingType(),
	                    pricing.getUnitPrice());

	            return PricingInfo.builder()
	                    .pricingType(pricing.getPricingType())
	                    .unitPrice(pricing.getUnitPrice())
	                    .minCharge(pricing.getMinCharge())
	                    .maxCharge(pricing.getMaxCharge())
	                    .source("EMPLOYER")
	                    .build();
	        }

	        log.debug(
	                "Employer check pricing not found → fallback to platform");
	    }


	    /*
	     * ------------------------------------------------------------
	     * Platform check pricing
	     * ------------------------------------------------------------
	     */
	    Optional<PlatformCheckPricing> platformPricing =
	            platformCheckPricingRepository
	                    .findByCheckCategory_CategoryIdAndRuleType_RuleTypeIdAndActiveTrue(
	                            categoryId,
	                            ruleType.getRuleTypeId());

	    if (platformPricing.isPresent()) {

	        PlatformCheckPricing pricing =
	                platformPricing.get();

	        log.debug(
	                "Platform check pricing found → pricingType={}, unitPrice={}",
	                pricing.getPricingType(),
	                pricing.getUnitPrice());

	        return PricingInfo.builder()
	                .pricingType(pricing.getPricingType())
	                .unitPrice(pricing.getUnitPrice())
	                .source("PLATFORM")
	                .build();
	    }

	    log.warn(
	            "No check pricing found → categoryId={}, ruleTypeId={}",
	            categoryId,
	            ruleType.getRuleTypeId());

	    return PricingInfo.builder()
	            .source("DEFAULT")
	            .unitPrice(BigDecimal.ZERO)
	            .build();
	}
	


	public PricingInfo getDocumentPrice(Long companyId, Long categoryId, Long documentTypeId) {

		// 🔹 1. Try Employer-specific pricing
		Optional<EmployerDocumentPricing> employerPricing = employerDocumentPricingRepository
				.findByCompany_IdAndCheckCategory_CategoryIdAndDocumentType_docTypeId(companyId,
						categoryId, documentTypeId);

		if (employerPricing.isPresent()) {
			return PricingInfo.builder().unitPrice(employerPricing.get().getUnitPrice()).source("EMPLOYER").build();
		}

		// 🔹 2. Fallback to Platform pricing
		Optional<PlatformDocumentPricing> platformPricing = platformDocumentPricingRepository
				.findByCheckCategory_CategoryIdAndDocumentType_DocTypeIdAndActiveTrue(categoryId, documentTypeId);

		if (platformPricing.isPresent()) {
			return PricingInfo.builder().unitPrice(platformPricing.get().getUnitPrice()).source("PLATFORM").build();
		}

		// 🔹 3. Default fallback
		return PricingInfo.builder().unitPrice(BigDecimal.ZERO).source("DEFAULT").build();
	}
}
