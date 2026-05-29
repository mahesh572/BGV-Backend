package com.org.bgv.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.common.RuleTypesDTO;
import com.org.bgv.common.RuleTypesRequest;
import com.org.bgv.common.RulesDocumentDTO;
import com.org.bgv.dto.CheckCategoryEnum;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.DocumentType;
import com.org.bgv.entity.RuleTypes;
import com.org.bgv.enums.RuleGroup;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.repository.DocumentTypeRepository;
import com.org.bgv.repository.RuleTypesRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class RuleTypesService {
	private final RuleTypesRepository ruleTypesRepository;
    private final CheckCategoryRepository checkCategoryRepository;
    private final DocumentTypeRepository documentTypeRepository;

    private RuleTypesDTO convertToDTO(RuleTypes rule) {
        return convertToDTO(rule, null);
    }
    
    @Transactional
    public RuleTypesDTO createRule(RuleTypesRequest request) {
        log.info("Creating new rule with code: {}", request.getCode());
        
        // Check if code already exists
        if (ruleTypesRepository.existsByCodeAndCategoryCategoryId(request.getCode(),request.getCategoryId())) {
            throw new RuntimeException("Rule with code " + request.getCode() + " already exists");
        }
        
        // Fetch category
        CheckCategory category = checkCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + request.getCategoryId()));
        
        // Create and save rule
        RuleTypes rule = RuleTypes.builder()
                .category(category)
                .name(request.getName())
                .code(request.getCode())
                .label(request.getLabel())
                .minCount(request.getMinCount())
                .maxCount(request.getMaxCount())
                .ruleGroup(request.getSelectedRulegroup())
                .requiresCount(request.getRequiresCount())
                .build();
        
        RuleTypes savedRule = ruleTypesRepository.save(rule);
        log.info("Rule created successfully with ID: {}", savedRule.getRuleTypeId());
        
        return convertToDTO(savedRule,null);
    }

    
    public RuleTypesDTO getRuleById(Long ruleTypeId) {
        log.debug("Fetching rule by ID: {}", ruleTypeId);
        
        RuleTypes rule = ruleTypesRepository.findById(ruleTypeId)
                .orElseThrow(() -> new RuntimeException("Rule not found with id: " + ruleTypeId));
        
        return convertToDTO(rule,null);
    }

    
    public List<RuleTypesDTO> getAllRules() {
        log.debug("Fetching all rules");
        
        return ruleTypesRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    
    public List<RuleTypesDTO> getRulesByCategory(Long categoryId) {
        log.debug("Fetching rules for category ID: {}", categoryId);
        
        List<DocumentType> documentTypes = documentTypeRepository.findByCategoryCategoryId(categoryId);
        
        
        return ruleTypesRepository.findByCategoryCategoryId(categoryId)
                .stream()
                .map(r -> convertToDTO(r,documentTypes))
                .collect(Collectors.toList());
    }

    
    @Transactional
    public RuleTypesDTO updateRule(Long ruleTypeId, RuleTypesRequest request) {
        log.info("Updating rule with ID: {}", ruleTypeId);

        RuleTypes existingRule = ruleTypesRepository.findById(ruleTypeId)
                .orElseThrow(() -> new RuntimeException("Rule not found with id: " + ruleTypeId));

        // Only check duplicate if code or category is changed
        if (!Objects.equals(existingRule.getCode(), request.getCode()) ||
        	    !Objects.equals(existingRule.getCategory().getCategoryId(), request.getCategoryId())) {

            boolean exists = ruleTypesRepository
                    .existsByCodeAndCategoryCategoryId(
                            request.getCode(),
                            request.getCategoryId()
                    );

            if (exists) {
                throw new RuntimeException(
                        "Rule with code " + request.getCode() + " already exists"
                );
            }
        }

        // Fetch category if changed
        if (!existingRule.getCategory().getCategoryId()
                .equals(request.getCategoryId())) {

            CheckCategory category = checkCategoryRepository
                    .findById(request.getCategoryId())
                    .orElseThrow(() ->
                            new RuntimeException("Category not found with id: "
                                    + request.getCategoryId()));

            existingRule.setCategory(category);
        }

        // Update fields
        existingRule.setName(request.getName());
        existingRule.setCode(request.getCode());
        existingRule.setLabel(request.getLabel());
        existingRule.setMinCount(request.getMinCount());
        existingRule.setMaxCount(request.getMaxCount());
        existingRule.setRuleGroup(request.getSelectedRulegroup());
        existingRule.setRequiresCount(request.getRequiresCount());
        existingRule.setDocumentTypeId(request.getDocumentTypeId());

        RuleTypes updatedRule = ruleTypesRepository.save(existingRule);

        log.info("Rule updated successfully with ID: {}", updatedRule.getRuleTypeId());

        return convertToDTO(updatedRule,null);
    }

    
    @Transactional
    public void deleteRule(Long ruleTypeId) {
        log.info("Deleting rule with ID: {}", ruleTypeId);
        
        // Check if rule exists
        if (!ruleTypesRepository.existsById(ruleTypeId)) {
            throw new RuntimeException("Rule not found with id: " + ruleTypeId);
        }
        
        ruleTypesRepository.deleteById(ruleTypeId);
        log.info("Rule deleted successfully with ID: {}", ruleTypeId);
    }

    
    public RuleTypesDTO getRuleByCode(String code) {
        log.debug("Fetching rule by code: {}", code);
        
        RuleTypes rule = ruleTypesRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Rule not found with code: " + code));
        
        return convertToDTO(rule,null);
    }

    // Helper method to convert Entity to DTO
    private RuleTypesDTO convertToDTO(RuleTypes rule, List<DocumentType> documentTypes) {

        List<RulesDocumentDTO> documentDTOs = null;

        Long selectedDocId = rule.getDocumentTypeId(); // ✅ direct

        if (documentTypes != null && !documentTypes.isEmpty()) {
            documentDTOs = documentTypes.stream()
                    .map(doc -> RulesDocumentDTO.builder()
                            .documentTypeId(doc.getDocTypeId())
                            .name(doc.getName())
                            .code(doc.getCode())
                            .selected(
                                    selectedDocId != null &&
                                    selectedDocId.equals(doc.getDocTypeId())
                            ) // ✅ FIX
                            .build())
                    .collect(Collectors.toList());
        }

        return RuleTypesDTO.builder()
                .ruleTypeId(rule.getRuleTypeId())
                .categoryId(rule.getCategory().getCategoryId())
                .categoryName(rule.getCategory().getName())
                .name(rule.getName())
                .code(rule.getCode())
                .label(rule.getLabel())
                .minCount(rule.getMinCount())
                .maxCount(rule.getMaxCount())
                .ruleGroup(RuleGroup.getByCategory(rule.getCategory().getName()))
                .selectedRulegroup(
                        rule.getRuleGroup() != null
                                ? rule.getRuleGroup()
                                : RuleGroup.NONE
                )
                .requiresCount(rule.getRequiresCount())
                .documents(documentDTOs)
                .build();
    }
}
