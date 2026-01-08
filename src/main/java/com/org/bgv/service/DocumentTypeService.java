package com.org.bgv.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.org.bgv.common.CheckCategoryResponse;
import com.org.bgv.common.DocumentTypeRequest;
import com.org.bgv.common.DocumentTypeResponse;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.DocumentType;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.repository.DocumentTypeRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class DocumentTypeService {

	private final DocumentTypeRepository documentTypeRepository;
    private final CheckCategoryRepository checkCategoryRepository;

    private DocumentTypeResponse mapToResponse(DocumentType documentType) {
        CheckCategoryResponse categoryResponse = null;
        if (documentType.getCategory() != null) {
            categoryResponse = CheckCategoryResponse.builder()
                    .categoryId(documentType.getCategory().getCategoryId())
                    .name(documentType.getCategory().getName())
                    .description(documentType.getCategory().getDescription())
                    .label(documentType.getCategory().getLabel())
                    .code(documentType.getCategory().getCode())
                    .build();
        }

        return DocumentTypeResponse.builder()
                .docTypeId(documentType.getDocTypeId())
                .name(documentType.getName())
                .category(categoryResponse)
                .label(documentType.getLabel())
                .isRequired(documentType.isRequired())
                .upload(documentType.getUpload())
                .code(documentType.getCode())
                .price(documentType.getPrice())
                .build();
    }

    private DocumentType mapToEntity(DocumentTypeRequest request) {
        CheckCategory category = null;
        if (request.getCategoryId() != null) {
            category = checkCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("CheckCategory not found with id: " + request.getCategoryId()));
        }

        return DocumentType.builder()
                .name(request.getName())
                .category(category)
                .label(request.getLabel())
                .isRequired(request.getIsRequired() != null ? request.getIsRequired() : false)
                .upload(request.getUpload())
                .code(request.getCode())
                .price(request.getPrice())
                .build();
    }

    
    public List<DocumentTypeResponse> getAllDocumentTypes() {
        return documentTypeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    
    public Optional<DocumentTypeResponse> getDocumentTypeById(Long id) {
        return documentTypeRepository.findById(id)
                .map(this::mapToResponse);
    }

    
    public Optional<DocumentTypeResponse> getDocumentTypeByName(String name) {
        return documentTypeRepository.findByName(name)
                .map(this::mapToResponse);
    }

    
    public Optional<DocumentTypeResponse> getDocumentTypeByCode(String code) {
        return documentTypeRepository.findByCode(code)
                .map(this::mapToResponse);
    }

    
    public List<DocumentTypeResponse> getDocumentTypesByCategory(Long categoryId) {
        return documentTypeRepository.findByCategoryCategoryId(categoryId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    
    public List<DocumentTypeResponse> getDocumentTypesByCategoryName(String categoryName) {
        return documentTypeRepository.findByCategoryName(categoryName)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    
    public List<DocumentTypeResponse> getRequiredDocumentTypes(boolean isRequired) {
        return documentTypeRepository.findByIsRequired(isRequired)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    
    public DocumentTypeResponse createDocumentType(DocumentTypeRequest documentTypeRequest) {
        // Validate unique constraints
        if (documentTypeRepository.existsByName(documentTypeRequest.getName())) {
            throw new RuntimeException("DocumentType with name '" + documentTypeRequest.getName() + "' already exists");
        }
        if (documentTypeRepository.existsByCode(documentTypeRequest.getCode())) {
            throw new RuntimeException("DocumentType with code '" + documentTypeRequest.getCode() + "' already exists");
        }

        DocumentType documentType = mapToEntity(documentTypeRequest);
        DocumentType savedDocumentType = documentTypeRepository.save(documentType);
        return mapToResponse(savedDocumentType);
    }

    
    public DocumentTypeResponse updateDocumentType(Long id, DocumentTypeRequest documentTypeRequest) {
        return documentTypeRepository.findById(id)
                .map(existingDocumentType -> {
                    // Check if name is being changed and if it conflicts with existing
                    if (!existingDocumentType.getName().equals(documentTypeRequest.getName()) && 
                        documentTypeRepository.existsByName(documentTypeRequest.getName())) {
                        throw new RuntimeException("DocumentType with name '" + documentTypeRequest.getName() + "' already exists");
                    }
                    
                    // Check if code is being changed and if it conflicts with existing
                    if (!existingDocumentType.getCode().equals(documentTypeRequest.getCode()) && 
                        documentTypeRepository.existsByCode(documentTypeRequest.getCode())) {
                        throw new RuntimeException("DocumentType with code '" + documentTypeRequest.getCode() + "' already exists");
                    }

                    // Update category if provided
                    if (documentTypeRequest.getCategoryId() != null) {
                        CheckCategory category = checkCategoryRepository.findById(documentTypeRequest.getCategoryId())
                                .orElseThrow(() -> new RuntimeException("CheckCategory not found with id: " + documentTypeRequest.getCategoryId()));
                        existingDocumentType.setCategory(category);
                    }

                    existingDocumentType.setName(documentTypeRequest.getName());
                    existingDocumentType.setLabel(documentTypeRequest.getLabel());
                    existingDocumentType.setRequired(documentTypeRequest.getIsRequired() != null ? documentTypeRequest.getIsRequired() : false);
                    existingDocumentType.setUpload(documentTypeRequest.getUpload());
                    existingDocumentType.setCode(documentTypeRequest.getCode());
                    existingDocumentType.setPrice(documentTypeRequest.getPrice());

                    DocumentType updatedDocumentType = documentTypeRepository.save(existingDocumentType);
                    return mapToResponse(updatedDocumentType);
                })
                .orElseThrow(() -> new RuntimeException("DocumentType not found with id: " + id));
    }

    
    public void deleteDocumentType(Long id) {
        if (!documentTypeRepository.existsById(id)) {
            throw new RuntimeException("DocumentType not found with id: " + id);
        }
        documentTypeRepository.deleteById(id);
    }

    
    public boolean existsByName(String name) {
        return documentTypeRepository.existsByName(name);
    }

    
    public boolean existsByCode(String code) {
        return documentTypeRepository.existsByCode(code);
    }
	
	
	
	
}
