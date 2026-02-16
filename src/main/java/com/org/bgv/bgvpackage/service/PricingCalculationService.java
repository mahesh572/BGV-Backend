package com.org.bgv.bgvpackage.service;



import java.util.Optional;

import org.springframework.stereotype.Service;

import com.org.bgv.bgvpackage.entity.EmployerCheckPricing;
import com.org.bgv.bgvpackage.entity.PlatformCheckPricing;
import com.org.bgv.bgvpackage.repository.EmployerCheckPricingRepository;
import com.org.bgv.bgvpackage.repository.PlatformCheckPricingRepository;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.RuleTypes;
import com.org.bgv.enums.PricingType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PricingCalculationService {

    private final EmployerCheckPricingRepository employerPricingRepo;
    private final PlatformCheckPricingRepository platformPricingRepo;

    public Double calculateCheckPrice(
            Company company,
            CheckCategory category,
            RuleTypes ruleType,
            int recordCount
    ) {

        // 1️⃣ Try Employer Pricing First
        Optional<EmployerCheckPricing> employerPricingOpt =
                employerPricingRepo.findByCompanyAndCheckCategoryAndRuleTypeAndActiveTrue(
                		company, category, ruleType);

        if (employerPricingOpt.isPresent()) {
            return computePrice(
                    employerPricingOpt.get().getPricingType(),
                    employerPricingOpt.get().getUnitPrice(),
                    recordCount
            );
        }

        // 2️⃣ Fallback to Platform Pricing
        PlatformCheckPricing platformPricing =
                platformPricingRepo.findByCheckCategoryAndRuleTypeAndActiveTrue(
                        category, ruleType)
                        .orElseThrow(() ->
                                new RuntimeException("Pricing not configured for "
                                        + category.getName()));

        return computePrice(
                platformPricing.getPricingType(),
                platformPricing.getUnitPrice(),
                recordCount
        );
    }

    private Double computePrice(PricingType pricingType,
                                Double unitPrice,
                                int recordCount) {

        if (pricingType == PricingType.FLAT) {
            return unitPrice;
        }

        if (pricingType == PricingType.PER_RECORD) {
            return unitPrice * recordCount;
        }

        throw new RuntimeException("Invalid pricing type");
    }
}
