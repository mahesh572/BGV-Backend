package com.org.bgv.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.org.bgv.common.CheckCategoryResponse;
import com.org.bgv.common.DocumentAttributeRequest;
import com.org.bgv.common.DocumentAttributeResponse;
import com.org.bgv.common.DocumentTypeRequest;
import com.org.bgv.common.DocumentTypeResponse;
import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.DocumentAttribute;
import com.org.bgv.entity.DocumentType;
import com.org.bgv.entity.DocumentTypeAttribute;
import com.org.bgv.repository.CheckCategoryRepository;
import com.org.bgv.repository.DocumentAttributeRepository;
import com.org.bgv.repository.DocumentTypeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class DocumentTypeService {

	private final DocumentTypeRepository documentTypeRepository;
    private final CheckCategoryRepository checkCategoryRepository;
    private final DocumentAttributeRepository documentAttributeRepository;

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
        
        List<DocumentAttributeResponse> attributes =
        	    documentType.getAttributes().stream()
        	        .map(a -> DocumentAttributeResponse.builder()
        	            .attributeId(a.getAttribute().getId())
        	            .code(a.getAttribute().getCode())
        	            .label(a.getAttribute().getLabel())
        	            .mandatory(a.getMandatory())
        	            .maxFiles(a.getMaxFiles())
        	            .sequence(a.getSequence())
        	            .build())
        	        .toList();
        
        

        return DocumentTypeResponse.builder()
                .docTypeId(documentType.getDocTypeId())
                .name(documentType.getName())
                .category(categoryResponse)
                .label(documentType.getLabel())
                .isRequired(documentType.isRequired())
                .upload(documentType.getUpload())
                .code(documentType.getCode())
                .price(documentType.getPrice())
                .attributes(attributes)
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

    
    @Transactional
    public DocumentTypeResponse updateDocumentType(
            Long id,
            DocumentTypeRequest request
    ) {

        DocumentType documentType = documentTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DocumentType not found with id: " + id));

        // -----------------------------
        // 1️⃣ Uniqueness checks
        // -----------------------------
        if (!documentType.getName().equals(request.getName())
                && documentTypeRepository.existsByName(request.getName())) {
            throw new RuntimeException("DocumentType with name already exists");
        }

        if (!documentType.getCode().equals(request.getCode())
                && documentTypeRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("DocumentType with code already exists");
        }

        // -----------------------------
        // 2️⃣ Update base fields
        // -----------------------------
        documentType.setName(request.getName());
        documentType.setCode(request.getCode());
        documentType.setLabel(request.getLabel());
        documentType.setRequired(Boolean.TRUE.equals(request.getIsRequired()));
        documentType.setUpload(request.getUpload());
        documentType.setPrice(request.getPrice());

        if (request.getCategoryId() != null) {
            CheckCategory category = checkCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            documentType.setCategory(category);
        }

        // -----------------------------
        // 3️⃣ Attribute handling
        // -----------------------------
        Map<Long, DocumentTypeAttribute> existingMap =
                documentType.getAttributes()
                        .stream()
                        .collect(Collectors.toMap(
                                a -> a.getAttribute().getId(),
                                Function.identity()
                        ));

        List<DocumentTypeAttribute> updatedMappings = new ArrayList();

        for (DocumentAttributeRequest attrReq : request.getAttributes()) {

            // 🔹 Resolve attribute master
            DocumentAttribute master;

            if (attrReq.getAttributeId() != null) {
                master = documentAttributeRepository.findById(attrReq.getAttributeId())
                        .orElseThrow(() -> new RuntimeException("Attribute not found"));
            } else {
                master = documentAttributeRepository
                        .findByCode(attrReq.getCode())
                        .orElseGet(() -> documentAttributeRepository.save(
                                DocumentAttribute.builder()
                                        .code(attrReq.getCode())
                                        .label(attrReq.getLabel())
                                        .type(attrReq.getType())
                                        .active(Boolean.TRUE.equals(attrReq.getActive()))
                                        .build()
                        ));
            }

            // 🔹 Resolve or create mapping
            DocumentTypeAttribute mapping =
                    existingMap.getOrDefault(
                            master.getId(),
                            new DocumentTypeAttribute()
                    );

            mapping.setDocumentType(documentType);
            mapping.setAttribute(master);
            mapping.setMandatory(Boolean.TRUE.equals(attrReq.getMandatory()));
            mapping.setMaxFiles(attrReq.getMaxFiles());
            mapping.setSequence(attrReq.getSequence());

            updatedMappings.add(mapping);
        }

        // -----------------------------
        // 4️⃣ Replace mappings (delete removed ones)
        // -----------------------------
        documentType.getAttributes().clear();
        documentType.getAttributes().addAll(updatedMappings);

        // -----------------------------
        // 5️⃣ Save
        // -----------------------------
        DocumentType saved = documentTypeRepository.save(documentType);

        return mapToResponse(saved);
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
