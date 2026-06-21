package com.org.bgv.bgvpackage.service;

import java.math.BigDecimal;
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
import com.org.bgv.company.entity.EmployerDocumentPricing;
import com.org.bgv.company.repository.EmployerDocumentPricingRepository;
import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.DocumentType;
import com.org.bgv.entity.RuleTypes;
import com.org.bgv.enums.PricingType;
import com.org.bgv.onboarding.entity.Company;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.repository.CompanyRepository;
import com.org.bgv.repository.DocumentTypeRepository;
import com.org.bgv.repository.RuleTypesRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployerCheckPricingService {

    private final EmployerCheckPricingRepository employerRepo;
    private final PlatformCheckPricingRepository platformRepo;
    private final CompanyRepository companyRepository;
    private final CheckCategoryRepository categoryRepository;
    private final RuleTypesRepository ruleTypesRepository;
    private final EmployerDocumentPricingRepository employerDocumentPricingRepository;
    private final DocumentTypeRepository documentTypeRepository;

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

        // ==========================================================
        // 🔵 IDENTITY CATEGORY (Document Based)
        // ==========================================================
        if (category.getCode().equalsIgnoreCase("IDENTITY")) {

            if (request.getRuleTypeId() == null) {
                throw new RuntimeException("DocumentTypeId required for identity category");
            }

            DocumentType documentType = documentTypeRepository.findById(
                    request.getRuleTypeId()
            ).orElseThrow(() -> new RuntimeException("Document not found"));

            EmployerDocumentPricing pricing =
                    employerDocumentPricingRepository
                            .findByCompany_IdAndCheckCategory_CategoryIdAndDocumentType_docTypeId(
                                    request.getCompanyId(),
                                    request.getCheckCategoryId(),
                                    request.getRuleTypeId()
                            )
                            .orElse(null);

            if (pricing != null) {
                pricing.setPricingType(request.getPricingType());
                pricing.setUnitPrice(request.getUnitPrice());
                pricing.setMinCharge(request.getMinCharge());
                pricing.setMaxCharge(request.getMaxCharge());
            } else {
                pricing = new EmployerDocumentPricing();
                pricing.setCompany(company);
                pricing.setCheckCategory(category);
                pricing.setDocumentType(documentType);
                pricing.setPricingType(request.getPricingType());
                pricing.setUnitPrice(request.getUnitPrice());
                pricing.setMinCharge(request.getMinCharge());
                pricing.setMaxCharge(request.getMaxCharge());
                pricing.setActive(true);
            }

            employerDocumentPricingRepository.save(pricing);

            return mapDocumentToResponse(pricing);
        }

        // ==========================================================
        // 🟢 RULE TYPE CATEGORY
        // ==========================================================
        if (request.getRuleTypeId() == null) {
            throw new RuntimeException("RuleTypeId required for this category");
        }

        RuleTypes ruleType = ruleTypesRepository.findById(request.getRuleTypeId())
                .orElseThrow(() -> new RuntimeException("Rule type not found"));

        EmployerCheckPricing pricing =
                employerRepo
                        .findByCompany_IdAndCheckCategory_CategoryIdAndRuleType_RuleTypeIdAndActiveTrue(
                                request.getCompanyId(),
                                request.getCheckCategoryId(),
                                request.getRuleTypeId()
                        )
                        .orElse(null);

        if (pricing != null) {
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
    public BigDecimal  resolveFinalPrice(
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
    
    
    
    @Transactional
    public void saveOrUpdateEmployerDocumentPricing(
            Long companyId,
            Long categoryId,
            Long documentTypeId,
            PricingType pricingType,
            BigDecimal  unitPrice
    ) {

        EmployerDocumentPricing existing =
                employerDocumentPricingRepository
                        .findByCompany_IdAndCheckCategory_CategoryIdAndDocumentType_docTypeId(
                                companyId, categoryId, documentTypeId)
                        .orElse(null);

        if (existing != null) {
            existing.setPricingType(pricingType);
            existing.setUnitPrice(unitPrice);
        } else {

            EmployerDocumentPricing newDocPricing = new EmployerDocumentPricing();
            newDocPricing.setCompany(companyRepository.getReferenceById(companyId));
            newDocPricing.setCheckCategory(categoryRepository.getReferenceById(categoryId));
            newDocPricing.setDocumentType(documentTypeRepository.getReferenceById(documentTypeId));
            newDocPricing.setPricingType(pricingType);
            newDocPricing.setUnitPrice(unitPrice);
            newDocPricing.setActive(true);

            employerDocumentPricingRepository.save(newDocPricing);
        }
    }
    
    @Transactional
    public void saveOrUpdateEmployerCheckPricing(
            Long companyId,
            Long categoryId,
            Long ruleTypeId,
            PricingType pricingType,
            BigDecimal unitPrice
    ) {

        EmployerCheckPricing existing =
        		employerRepo
                        .findByCompany_IdAndCheckCategory_CategoryIdAndRuleType_RuleTypeId(
                                companyId, categoryId, ruleTypeId)
                        .orElse(null);

        if (existing != null) {
            existing.setPricingType(pricingType);
            existing.setUnitPrice(unitPrice);
        } else {

            EmployerCheckPricing newPricing = EmployerCheckPricing.builder()
                    .company(companyRepository.getReferenceById(companyId))
                    .checkCategory(categoryRepository.getReferenceById(categoryId))
                    .ruleType(ruleTypesRepository.getReferenceById(ruleTypeId))
                    .pricingType(pricingType)
                    .unitPrice(unitPrice)
                    .active(true)
                    .build();

            employerRepo.save(newPricing);
        }
    }
    
    
    @Transactional(readOnly = true)
    public List<EmployerCheckPricingResponse> getEmployerPricingByCompanyAndCategory(
            Long companyId,
            Long categoryId
    ) {

        CheckCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // ==========================================================
        // 🔵 IDENTITY → DOCUMENT BASED
        // ==========================================================
        if (category.getName().equalsIgnoreCase(CheckCategoryEnum.IDENTITY.getName())) {

            List<EmployerDocumentPricing> employerDocs =
                    employerDocumentPricingRepository
                            .findByCompany_IdAndCheckCategory_CategoryIdAndActiveTrue(
                                    companyId, categoryId);
            
            log.info("EmployerCheckPricingService::::::::::::::::::::::{}",employerDocs.size());

            return employerDocs.stream()
                    .map(doc -> EmployerCheckPricingResponse.builder()
                            .id(doc.getId())
                            .companyId(doc.getCompany().getId())
                            .companyName(doc.getCompany().getCompanyName())
                            .checkCategoryId(category.getCategoryId())
                            .checkCategoryName(category.getName())
                            .checkCategoryCode(category.getCode())

                            // Document-based → ruleType fields null
                            .ruleTypeId(doc.getDocumentType().getDocTypeId())
                            .ruleTypeName(doc.getDocumentType().getName())
                            .ruleTypeCode(doc.getDocumentType().getCode())

                            .pricingType(doc.getPricingType())
                            .unitPrice(doc.getUnitPrice())
                            .minCharge(doc.getMinCharge())
                            .maxCharge(doc.getMaxCharge())
                            .active(doc.getActive())
                            .build()
                    )
                    .toList();
        }

        // ==========================================================
        // 🟢 RULE TYPE BASED
        // ==========================================================
        return employerRepo
                .findByCompany_IdAndCheckCategory_CategoryIdAndActiveTrue(
                        companyId, categoryId)
                .stream()
                .map(pricing -> EmployerCheckPricingResponse.builder()
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
                        .build()
                )
                .toList();
    }
    
    
    private EmployerCheckPricingResponse mapDocumentToResponse(
            EmployerDocumentPricing pricing
    ) {

        return EmployerCheckPricingResponse.builder()
                .id(pricing.getId())
                .companyId(pricing.getCompany().getId())
                .companyName(pricing.getCompany().getCompanyName())
                .checkCategoryId(pricing.getCheckCategory().getCategoryId())
                .checkCategoryName(pricing.getCheckCategory().getName())
                .checkCategoryCode(pricing.getCheckCategory().getCode())

                // Document-based mapping
                .ruleTypeId(null)
                .ruleTypeName(pricing.getDocumentType().getName())
                .ruleTypeCode(pricing.getDocumentType().getCode())

                .pricingType(pricing.getPricingType())
                .unitPrice(pricing.getUnitPrice())
                .minCharge(pricing.getMinCharge())
                .maxCharge(pricing.getMaxCharge())
                .active(pricing.getActive())
                .build();
    }
    
}
