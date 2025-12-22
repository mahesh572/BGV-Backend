package com.org.bgv.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.org.bgv.common.CheckCategoryRequest;
import com.org.bgv.common.CheckCategoryResponse;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.repository.CheckCategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CheckCategoryService {
	
	private final CheckCategoryRepository checkCategoryRepository;
	
	private CheckCategoryResponse mapToResponse(CheckCategory checkCategory) {
        return CheckCategoryResponse.builder()
                .categoryId(checkCategory.getCategoryId())
                .name(checkCategory.getName())
                .description(checkCategory.getDescription())
                .label(checkCategory.getLabel())
                .code(checkCategory.getCode())
                .hasDocuments(checkCategory.getHasDocuments())
                .isActive(checkCategory.getIsActive())
                .price(checkCategory.getPrice())
                .build();
    }

    private CheckCategory mapToEntity(CheckCategoryRequest request) {
        return CheckCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .label(request.getLabel())
                .code(request.getCode())
                .hasDocuments(request.getHasDocuments())
                .isActive(request.getIsActive())
                .price(request.getPrice())
                .build();
    }

    
    public List<CheckCategoryResponse> getAllCheckCategories() {
        return checkCategoryRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

   
    public Optional<CheckCategoryResponse> getCheckCategoryById(Long id) {
        return checkCategoryRepository.findById(id)
                .map(this::mapToResponse);
    }

    
    public Optional<CheckCategoryResponse> getCheckCategoryByName(String name) {
        return checkCategoryRepository.findByName(name)
                .map(this::mapToResponse);
    }

    
    public CheckCategoryResponse getCheckCategoryByCode(String code) {
        return mapToResponse(checkCategoryRepository.findByCode(code));
    }

    
    public CheckCategoryResponse createCheckCategory(CheckCategoryRequest checkCategoryRequest) {
        // Validate unique constraints
        if (checkCategoryRepository.existsByName(checkCategoryRequest.getName())) {
            throw new RuntimeException("Category with name '" + checkCategoryRequest.getName() + "' already exists");
        }
        if (checkCategoryRepository.existsByCode(checkCategoryRequest.getCode())) {
            throw new RuntimeException("Category with code '" + checkCategoryRequest.getCode() + "' already exists");
        }
        
        CheckCategory checkCategory = mapToEntity(checkCategoryRequest);
        CheckCategory savedCategory = checkCategoryRepository.save(checkCategory);
        return mapToResponse(savedCategory);
    }

    
    public CheckCategoryResponse updateCheckCategory(Long id, CheckCategoryRequest checkCategoryRequest) {
        return checkCategoryRepository.findById(id)
                .map(existingCategory -> {
                    // Check if name is being changed and if it conflicts with existing
                    /*
                	
                	if (!existingCategory.getName().equals(checkCategoryRequest.getName()) && 
                        checkCategoryRepository.existsByName(checkCategoryRequest.getName())) {
                        throw new RuntimeException("Category with name '" + checkCategoryRequest.getName() + "' already exists");
                    }
                    
                    // Check if code is being changed and if it conflicts with existing
                    if (!existingCategory.getCode().equals(checkCategoryRequest.getCode()) && 
                        checkCategoryRepository.existsByCode(checkCategoryRequest.getCode())) {
                        throw new RuntimeException("Category with code '" + checkCategoryRequest.getCode() + "' already exists");
                    }
                    */
                    existingCategory.setName(checkCategoryRequest.getName());
                    existingCategory.setDescription(checkCategoryRequest.getDescription());
                    existingCategory.setLabel(checkCategoryRequest.getLabel());
                    existingCategory.setCode(checkCategoryRequest.getCode());
                    existingCategory.setHasDocuments(checkCategoryRequest.getHasDocuments());
                    existingCategory.setIsActive(checkCategoryRequest.getIsActive());
                    existingCategory.setPrice(checkCategoryRequest.getPrice());
                    
                    CheckCategory updatedCategory = checkCategoryRepository.save(existingCategory);
                    return mapToResponse(updatedCategory);
                })
                .orElseThrow(() -> new RuntimeException("CheckCategory not found with id: " + id));
    }

    
    public void deleteCheckCategory(Long id) {
        if (!checkCategoryRepository.existsById(id)) {
            throw new RuntimeException("CheckCategory not found with id: " + id);
        }
        checkCategoryRepository.deleteById(id);
    }

   
    public boolean existsByName(String name) {
        return checkCategoryRepository.existsByName(name);
    }

    
    public boolean existsByCode(String code) {
        return checkCategoryRepository.existsByCode(code);
    }
	
}
