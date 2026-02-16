package com.org.bgv.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.common.RuleTypesDTO;
import com.org.bgv.common.RuleTypesRequest;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.RuleTypes;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.repository.RuleTypesRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class RuleTypesService {
	private final RuleTypesRepository ruleTypesRepository;
    private final CheckCategoryRepository checkCategoryRepository;

    
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
                .build();
        
        RuleTypes savedRule = ruleTypesRepository.save(rule);
        log.info("Rule created successfully with ID: {}", savedRule.getRuleTypeId());
        
        return convertToDTO(savedRule);
    }

    
    public RuleTypesDTO getRuleById(Long ruleTypeId) {
        log.debug("Fetching rule by ID: {}", ruleTypeId);
        
        RuleTypes rule = ruleTypesRepository.findById(ruleTypeId)
                .orElseThrow(() -> new RuntimeException("Rule not found with id: " + ruleTypeId));
        
        return convertToDTO(rule);
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
        
        return ruleTypesRepository.findByCategoryCategoryId(categoryId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    
    @Transactional
    public RuleTypesDTO updateRule(Long ruleTypeId, RuleTypesRequest request) {
        log.info("Updating rule with ID: {}", ruleTypeId);

        RuleTypes existingRule = ruleTypesRepository.findById(ruleTypeId)
                .orElseThrow(() -> new RuntimeException("Rule not found with id: " + ruleTypeId));

        // Only check duplicate if code or category is changed
        if (!existingRule.getCode().equals(request.getCode()) ||
            !existingRule.getCategory().getCategoryId().equals(request.getCategoryId())) {

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

        RuleTypes updatedRule = ruleTypesRepository.save(existingRule);

        log.info("Rule updated successfully with ID: {}", updatedRule.getRuleTypeId());

        return convertToDTO(updatedRule);
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
        
        return convertToDTO(rule);
    }

    // Helper method to convert Entity to DTO
    private RuleTypesDTO convertToDTO(RuleTypes rule) {
        return RuleTypesDTO.builder()
                .ruleTypeId(rule.getRuleTypeId())
                .categoryId(rule.getCategory().getCategoryId())
                .categoryName(rule.getCategory().getName())
                .name(rule.getName())
                .code(rule.getCode())
                .label(rule.getLabel())
                .minCount(rule.getMinCount())
                .maxCount(rule.getMaxCount())
                .build();
    }
}
