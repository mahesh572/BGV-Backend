package com.org.bgv.bgvpackage.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.bgvpackage.dto.EmployerCheckPricingRequest;
import com.org.bgv.bgvpackage.dto.EmployerCheckPricingResponse;
import com.org.bgv.bgvpackage.entity.EmployerCheckPricing;
import com.org.bgv.bgvpackage.entity.PlatformCheckPricing;
import com.org.bgv.bgvpackage.repository.EmployerCheckPricingRepository;
import com.org.bgv.bgvpackage.repository.PlatformCheckPricingRepository;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.Company;
import com.org.bgv.entity.RuleTypes;
import com.org.bgv.enums.PricingType;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.repository.CompanyRepository;
import com.org.bgv.repository.RuleTypesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployerCheckPricingService {

    private final EmployerCheckPricingRepository employerRepo;
    private final PlatformCheckPricingRepository platformRepo;
    private final CompanyRepository companyRepository;
    private final CheckCategoryRepository categoryRepository;
    private final RuleTypesRepository ruleTypesRepository;

    // ======================================================
    // CREATE OR UPDATE
    // ======================================================
    @Transactional
    public EmployerCheckPricingResponse createOrUpdate(
            EmployerCheckPricingRequest request
    ) {

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        CheckCategory category = categoryRepository.findById(request.getCheckCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        RuleTypes ruleType = ruleTypesRepository.findById(request.getRuleTypeId())
                .orElseThrow(() -> new RuntimeException("Rule type not found"));

        Optional<EmployerCheckPricing> existing =
                employerRepo.findByCompany_IdAndCheckCategory_CategoryIdAndRuleType_RuleTypeIdAndActiveTrue(
                        request.getCompanyId(),
                        request.getCheckCategoryId(),
                        request.getRuleTypeId()
                );

        EmployerCheckPricing pricing;

        if (existing.isPresent()) {
            pricing = existing.get();
            pricing.setPricingType(request.getPricingType());
            pricing.setUnitPrice(request.getUnitPrice());
            pricing.setMinCharge(request.getMinCharge());
            pricing.setMaxCharge(request.getMaxCharge());
        } else {
            pricing = EmployerCheckPricing.builder()
                    .company(company)
                    .checkCategory(category)
                    .ruleType(ruleType)
                    .pricingType(request.getPricingType())
                    .unitPrice(request.getUnitPrice())
                    .minCharge(request.getMinCharge())
                    .maxCharge(request.getMaxCharge())
                    .active(true)
                    .build();
        }

        pricing = employerRepo.save(pricing);

        return mapToResponse(pricing);
    }

    // ======================================================
    // GET BY COMPANY & CATEGORY
    // ======================================================
    @Transactional(readOnly = true)
    public List<EmployerCheckPricingResponse> getByCompanyAndCategory(
            Long companyId,
            Long categoryId
    ) {

        return employerRepo
                .findByCompany_IdAndCheckCategory_CategoryIdAndActiveTrue(
                        companyId, categoryId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ======================================================
    // 🔥 RESOLVE FINAL PRICE
    // ======================================================
    @Transactional(readOnly = true)
    public Double resolveFinalPrice(
            Long companyId,
            Long categoryId,
            Long ruleTypeId
    ) {

        return employerRepo
                .findByCompany_IdAndCheckCategory_CategoryIdAndRuleType_RuleTypeIdAndActiveTrue(
                        companyId, categoryId, ruleTypeId)
                .map(EmployerCheckPricing::getUnitPrice)
                .orElseGet(() ->
                        platformRepo
                                .findByCheckCategory_CategoryIdAndRuleType_RuleTypeIdAndActiveTrue(
                                        categoryId, ruleTypeId)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Pricing not configured"))
                                .getUnitPrice()
                );
    }

    // ======================================================
    // SOFT DELETE
    // ======================================================
    @Transactional
    public void deactivate(Long id) {

        EmployerCheckPricing pricing =
                employerRepo.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException("Pricing not found"));

        pricing.setActive(false);
        employerRepo.save(pricing);
    }

    // ======================================================
    // MAPPER
    // ======================================================
    private EmployerCheckPricingResponse mapToResponse(
            EmployerCheckPricing pricing
    ) {

        return EmployerCheckPricingResponse.builder()
                .id(pricing.getId())
                .companyId(pricing.getCompany().getId())
                .companyName(pricing.getCompany().getCompanyName())
                .checkCategoryId(pricing.getCheckCategory().getCategoryId())
                .checkCategoryName(pricing.getCheckCategory().getName())
                .checkCategoryCode(pricing.getCheckCategory().getCode())
                .ruleTypeId(pricing.getRuleType().getRuleTypeId())
                .ruleTypeName(pricing.getRuleType().getName())
                .ruleTypeCode(pricing.getRuleType().getCode())
                .pricingType(pricing.getPricingType())
                .unitPrice(pricing.getUnitPrice())
                .minCharge(pricing.getMinCharge())
                .maxCharge(pricing.getMaxCharge())
                .active(pricing.getActive())
                .build();
    }
}
