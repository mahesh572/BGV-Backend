package com.org.bgv.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.org.bgv.common.CheckCategoryRequest;
import com.org.bgv.common.CheckCategoryResponse;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.RuleTypes;
import com.org.bgv.repository.RuleTypesRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CheckCategoryMapper {
	
	
	private final RuleTypeMapper ruleTypeMapper;
	private final RuleTypesRepository ruleTypesRepository; // Add this
	
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
    
    public CheckCategoryResponse toDetailedResponse(CheckCategory category) {
        if (category == null) {
            return null;
        }
        
        // Manually fetch rule types for this category
        List<RuleTypes> ruleTypes = ruleTypesRepository.findByCategory(category);
        
        return CheckCategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .description(category.getDescription())
                .label(category.getLabel())
                .code(category.getCode())
                .ruleTypes(ruleTypeMapper.toResponseList(ruleTypes))
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
    
    
    
}