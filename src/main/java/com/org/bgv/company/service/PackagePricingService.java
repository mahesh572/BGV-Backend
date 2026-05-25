package com.org.bgv.company.service;

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
	            RuleTypes ruleType
	    ) {

	        log.info("Resolving pricing → companyId={}, categoryId={}, ruleTypeId={}, ruleCode={}, ruleGroup={}",
	                companyId,
	                categoryId,
	                ruleType.getRuleTypeId(),
	                ruleType.getCode(),
	                ruleType.getRuleGroup()
	        );

	        // ✅ 1. DOCUMENT_SELECTION → use DOCUMENT pricing
	        if (RuleGroup.DOCUMENT_SELECTION.equals(ruleType.getRuleGroup())) {

	            Long documentTypeId = ruleType.getDocumentTypeId();

	            log.info("DOCUMENT_SELECTION detected → documentTypeId={}", documentTypeId);

	            if (documentTypeId != null) {

	                // 🔹 Employer Document Pricing
	                Optional<EmployerDocumentPricing> employerDocPricing =
	                        employerDocumentPricingRepository
	                                .findByCompany_IdAndCheckCategory_CategoryIdAndDocumentType_docTypeId(
	                                        companyId,
	                                        categoryId,
	                                        documentTypeId
	                                );

	                if (employerDocPricing.isPresent()) {
	                    EmployerDocumentPricing pricing = employerDocPricing.get();

	                    log.info("EmployerDocumentPricing FOUND → pricingType={}, unitPrice={}",
	                            pricing.getPricingType(),
	                            pricing.getUnitPrice()
	                    );

	                    return new PricingInfo(
	                            pricing.getPricingType(),
	                            pricing.getUnitPrice(),
	                            pricing.getMinCharge(),
	                            pricing.getMaxCharge()
	                    );
	                } else {
	                    log.warn("EmployerDocumentPricing NOT FOUND → fallback to platform pricing");
	                }

	                // 🔹 Platform Document Pricing
	                Optional<PlatformDocumentPricing> platformDocPricing =
	                        platformDocumentPricingRepository
	                                .findByCheckCategory_CategoryIdAndDocumentType_DocTypeIdAndActiveTrue(
	                                        categoryId,
	                                        documentTypeId
	                                );

	                if (platformDocPricing.isPresent()) {
	                    PlatformDocumentPricing pricing = platformDocPricing.get();

	                    log.info("PlatformDocumentPricing FOUND → pricingType={}, unitPrice={}",
	                            pricing.getPricingType(),
	                            pricing.getUnitPrice()
	                    );

	                    return new PricingInfo(
	                            pricing.getPricingType(),
	                            pricing.getUnitPrice(),
	                            null,
	                            null
	                    );
	                } else {
	                    log.warn("PlatformDocumentPricing NOT FOUND → returning empty pricing");
	                }
	            } else {
	                log.error("DocumentTypeId is NULL for ruleTypeId={}", ruleType.getRuleTypeId());
	            }

	            return new PricingInfo(null, null, null, null);
	        }

	        // ✅ 2. NON-DOCUMENT rules → rule-based pricing
	        log.info("NON-DOCUMENT rule → using rule-based pricing");

	        Optional<EmployerCheckPricing> employerPricing =
	                employerCheckPricingRepository
	                        .findByCompany_IdAndCheckCategory_CategoryIdAndRuleType_RuleTypeIdAndActiveTrue(
	                                companyId,
	                                categoryId,
	                                ruleType.getRuleTypeId()
	                        );

	        if (employerPricing.isPresent()) {
	            EmployerCheckPricing pricing = employerPricing.get();

	            log.info("EmployerCheckPricing FOUND → pricingType={}, unitPrice={}",
	                    pricing.getPricingType(),
	                    pricing.getUnitPrice()
	            );

	            return new PricingInfo(
	                    pricing.getPricingType(),
	                    pricing.getUnitPrice(),
	                    pricing.getMinCharge(),
	                    pricing.getMaxCharge()
	            );
	        } else {
	            log.warn("EmployerCheckPricing NOT FOUND → fallback to platform pricing");
	        }

	        PlatformCheckPricing platformPricing =
	                platformCheckPricingRepository
	                        .findByCheckCategory_CategoryIdAndRuleType_RuleTypeIdAndActiveTrue(
	                                categoryId,
	                                ruleType.getRuleTypeId()
	                        )
	                        .orElse(null);

	        if (platformPricing != null) {
	            log.info("PlatformCheckPricing FOUND → pricingType={}, unitPrice={}",
	                    platformPricing.getPricingType(),
	                    platformPricing.getUnitPrice()
	            );
	        } else {
	            log.warn("PlatformCheckPricing NOT FOUND → returning empty pricing");
	        }

	        return new PricingInfo(
	                platformPricing != null ? platformPricing.getPricingType() : null,
	                platformPricing != null ? platformPricing.getUnitPrice() : null,
	                null,
	                null
	        );
	    }
	
	
}
