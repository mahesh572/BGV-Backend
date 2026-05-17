package com.org.bgv.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.org.bgv.bgvpackage.entity.PlatformCheckPricing;
import com.org.bgv.bgvpackage.repository.PlatformCheckPricingRepository;
import com.org.bgv.common.CheckCategoryRequest;
import com.org.bgv.common.CheckCategoryResponse;
import com.org.bgv.common.RuleTypeResponse;
import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.DocumentType;
import com.org.bgv.entity.PlatformDocumentPricing;
import com.org.bgv.entity.RuleTypes;
import com.org.bgv.enums.PricingLevel;
import com.org.bgv.pricing.dto.DocumentPricingDTO;
import com.org.bgv.repository.DocumentTypeRepository;
import com.org.bgv.repository.PlatformDocumentPricingRepository;
import com.org.bgv.repository.RuleTypesRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CheckCategoryMapper {
	
	
	private final RuleTypeMapper ruleTypeMapper;
	private final RuleTypesRepository ruleTypesRepository; // Add this
	private final DocumentTypeRepository documentTypeRepository;
	private final PlatformDocumentPricingRepository platformDocumentPricingRepository;
	private final PlatformCheckPricingRepository platformCheckPricingRepository;
	
    public CheckCategory toEntity(CheckCategoryRequest request) {
        if (request == null) {
            return null;
        }
        
        return CheckCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .label(request.getLabel())
                .code(request.getCode())
                .build();
    }
    
    public CheckCategoryResponse toResponse(CheckCategory category) {
        if (category == null) {
            return null;
        }
        
        return CheckCategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .description(category.getDescription())
                .label(category.getLabel())
                .code(category.getCode())
                .build();
    }
    
    public List<CheckCategoryResponse> toResponseList(List<CheckCategory> categories) {
        if (categories == null) {
            return null;
        }
        
        return categories.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public void updateEntityFromRequest(CheckCategoryRequest request, CheckCategory category) {
        if (request == null || category == null) {
            return;
        }
        
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setLabel(request.getLabel());
        category.setCode(request.getCode());
    }
    
    public CheckCategoryResponse toDetailedResponseWithPricing(CheckCategory category) {

        if (category == null) {
            return null;
        }
        
        Boolean hasDocuments = Boolean.FALSE; 
        PricingLevel pricingLevel = PricingLevel.RULE;
        
        if(category.getName().contains(CheckCategoryEnum.IDENTITY.getName())) { 
        	hasDocuments = Boolean.TRUE; 
        	pricingLevel = PricingLevel.DOCUMENT;
        
        }


        // DOCUMENT BASED PRICING
        if (hasDocuments) {

            List<DocumentType> documents =
                    documentTypeRepository
                            .findByCategoryCategoryId(category.getCategoryId());

            List<DocumentPricingDTO> documentDTOs = documents.stream()
                    .map(doc -> {

                        Optional<PlatformDocumentPricing> pricingOpt =
                                platformDocumentPricingRepository
                                        .findByCheckCategory_CategoryIdAndDocumentType_DocTypeIdAndActiveTrue(
                                                category.getCategoryId(),
                                                doc.getDocTypeId());

                        PlatformDocumentPricing pricing =
                                pricingOpt.orElse(null);

                        return DocumentPricingDTO.builder()
                                .pricingId(pricing != null ? pricing.getId() : null)
                                .categoryId(category.getCategoryId())
                                .categoryName(category.getName())
                                .documentTypeId(doc.getDocTypeId())
                                .documentName(doc.getName())
                                .documentCode(doc.getCode())
                                .documentLabel(doc.getLabel())
                                .pricingType(pricing != null ? pricing.getPricingType() : null)
                                .unitPrice(pricing != null ? pricing.getUnitPrice() : null)
                                .minCharge(pricing != null ? pricing.getMinCharge() : null)
                                .maxCharge(pricing != null ? pricing.getMaxCharge() : null)
                                .active(pricing != null ? pricing.getActive() : false)
                                .build();
                    })
                    .toList();
            
         // RULE BASED PRICING
            List<PlatformCheckPricing> rulePricing =
                    platformCheckPricingRepository
                            .findByCheckCategory_CategoryIdAndActiveTrue(
                                    category.getCategoryId());

            List<RuleTypeResponse> ruleResponses =
                    rulePricing.stream()
                            .map(this::mapToRulePricingResponse)
                            .toList();

            return CheckCategoryResponse.builder()
                    .categoryId(category.getCategoryId())
                    .name(category.getName())
                    .description(category.getDescription())
                    .label(category.getLabel())
                    .code(category.getCode())
                    .hasDocuments(true)
                    .pricingLevel(pricingLevel)
                    .documents(documentDTOs)
                    .ruleTypes(ruleResponses)
                    .build();
        }

        List<RuleTypes> ruleTypes =
                ruleTypesRepository.findByCategory(category);

        List<PlatformCheckPricing> pricingList =
                platformCheckPricingRepository
                        .findByCheckCategory_CategoryIdAndActiveTrue(
                                category.getCategoryId());
        
        Map<Long, PlatformCheckPricing> pricingMap =
                pricingList.stream()
                        .collect(Collectors.toMap(
                                p -> p.getRuleType().getRuleTypeId(),
                                Function.identity()
                        ));
        
        
        // RULE BASED PRICING
        
        /*
        List<PlatformCheckPricing> rulePricing =
                platformCheckPricingRepository
                        .findByCheckCategory_CategoryIdAndActiveTrue(
                                category.getCategoryId());
                                */
   /*
        List<RuleTypeResponse> ruleResponses =
                rulePricing.stream()
                        .map(this::mapToRulePricingResponse)
                        .toList();
                        */
        
        List<RuleTypeResponse> ruleResponses =
                ruleTypes.stream()
                        .map(rule -> {

                            PlatformCheckPricing pricing =
                                    pricingMap.get(rule.getRuleTypeId());

                            return RuleTypeResponse.builder()
                                    .pricingId(pricing != null ? pricing.getId() : null)
                                    .ruleTypeId(rule.getRuleTypeId())
                                    .name(rule.getName())
                                    .code(rule.getCode())
                                    .pricingType(pricing != null ? pricing.getPricingType() : null)
                                    .unitPrice(pricing != null ? pricing.getUnitPrice() : null)
                                    .active(pricing != null ? pricing.getActive() : false)
                                    .build();
                        })
                        .toList();
        
        

        return CheckCategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .description(category.getDescription())
                .label(category.getLabel())
                .code(category.getCode())
                .hasDocuments(false)
                .pricingLevel(pricingLevel)
                .ruleTypes(ruleResponses)
                .build();
    }
    
    public CheckCategoryResponse toDetailedResponse(CheckCategory category) {

        if (category == null) {
            return null;
        }
        
        Boolean hasDocuments = Boolean.FALSE; 
        PricingLevel pricingLevel = PricingLevel.RULE;
        
        if(category.getName().contains(CheckCategoryEnum.IDENTITY.getName())) { 
        	hasDocuments = Boolean.TRUE; 
        	pricingLevel = PricingLevel.DOCUMENT;
        
        }


        // DOCUMENT BASED PRICING
        if (hasDocuments) {

            List<DocumentType> documents =
                    documentTypeRepository
                            .findByCategoryCategoryId(category.getCategoryId());

            List<DocumentPricingDTO> documentDTOs = documents.stream()
                    .map(doc -> {

                        Optional<PlatformDocumentPricing> pricingOpt =
                                platformDocumentPricingRepository
                                        .findByCheckCategory_CategoryIdAndDocumentType_DocTypeIdAndActiveTrue(
                                                category.getCategoryId(),
                                                doc.getDocTypeId());

                        PlatformDocumentPricing pricing =
                                pricingOpt.orElse(null);

                        return DocumentPricingDTO.builder()
                                .pricingId(pricing != null ? pricing.getId() : null)
                                .categoryId(category.getCategoryId())
                                .categoryName(category.getName())
                                .documentTypeId(doc.getDocTypeId())
                                .documentName(doc.getName())
                                .documentCode(doc.getCode())
                                .documentLabel(doc.getLabel())
                                .pricingType(pricing != null ? pricing.getPricingType() : null)
                                .unitPrice(pricing != null ? pricing.getUnitPrice() : null)
                                .minCharge(pricing != null ? pricing.getMinCharge() : null)
                                .maxCharge(pricing != null ? pricing.getMaxCharge() : null)
                                .active(pricing != null ? pricing.getActive() : false)
                                .build();
                    })
                    .toList();
            
         // RULE BASED PRICING
            List<RuleTypes> ruleTypes =
                    ruleTypesRepository.findByCategory(category);

            List<RuleTypeResponse> ruleResponses = ruleTypes.stream()
                    .map(rule -> RuleTypeResponse.builder()
                            .ruleTypeId(rule.getRuleTypeId())
                            .name(rule.getName())
                            .code(rule.getCode())
                            .build())
                    .toList();

            return CheckCategoryResponse.builder()
                    .categoryId(category.getCategoryId())
                    .name(category.getName())
                    .description(category.getDescription())
                    .label(category.getLabel())
                    .code(category.getCode())
                    .hasDocuments(true)
                    .pricingLevel(pricingLevel)
                    .documents(documentDTOs)
                    .ruleTypes(ruleResponses)
                    .build();
        }

        // RULE BASED PRICING
        List<PlatformCheckPricing> rulePricing =
                platformCheckPricingRepository
                        .findByCheckCategory_CategoryIdAndActiveTrue(
                                category.getCategoryId());

        List<RuleTypeResponse> ruleResponses =
                rulePricing.stream()
                        .map(this::mapToRulePricingResponse)
                        .toList();

        return CheckCategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .description(category.getDescription())
                .label(category.getLabel())
                .code(category.getCode())
                .hasDocuments(false)
                .pricingLevel(pricingLevel)
                .ruleTypes(ruleResponses)
                .build();
    }
    
    private RuleTypeResponse mapToRulePricingResponse(
            PlatformCheckPricing pricing) {

        RuleTypes rule = pricing.getRuleType();

        return RuleTypeResponse.builder()
                .pricingId(pricing.getId())
                .ruleTypeId(rule.getRuleTypeId())
                .name(rule.getName())
                .code(rule.getCode())
                .pricingType(pricing.getPricingType())
                .unitPrice(pricing.getUnitPrice())
                .active(pricing.getActive())
                .requiresCount(rule.getRequiresCount())
                .build();
    }
    
    private DocumentPricingDTO mapToDocumentPricingDTO(
            PlatformDocumentPricing pricing) {

        DocumentType doc = pricing.getDocumentType();

        return DocumentPricingDTO.builder()
                .pricingId(pricing.getId())
                .categoryId(pricing.getCheckCategory().getCategoryId())
                .categoryName(pricing.getCheckCategory().getName())
                .documentTypeId(doc.getDocTypeId())
                .documentName(doc.getName())
                .documentCode(doc.getCode())
                .documentLabel(doc.getLabel())
                .pricingType(pricing.getPricingType())
                .unitPrice(pricing.getUnitPrice())
                .minCharge(pricing.getMinCharge())
                .maxCharge(pricing.getMaxCharge())
                .active(pricing.getActive())
                .build();
    }
    
    
    public List<CheckCategoryResponse> toDetailedResponseList(List<CheckCategory> categories) {
        if (categories == null) {
            return Collections.emptyList();
        }
        
        return categories.stream()
                .map(this::toDetailedResponse)
                .collect(Collectors.toList());
    }
    public List<CheckCategoryResponse> toDetailedResponseListWithPricing(List<CheckCategory> categories) {
        if (categories == null) {
            return Collections.emptyList();
        }
        
        return categories.stream()
                .map(this::toDetailedResponseWithPricing)
                .collect(Collectors.toList());
    }
    
    
}