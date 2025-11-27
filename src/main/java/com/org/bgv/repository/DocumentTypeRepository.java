package com.org.bgv.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.DocumentType;

public interface DocumentTypeRepository extends JpaRepository<DocumentType, Long> {
	
	Optional<DocumentType> findByNameAndCategory_CategoryId(String name, Long categoryId);
	
	// Find all document types by category
    List<DocumentType> findByCategory(CheckCategory category);
    
    // Find all document types by category ID
    List<DocumentType> findByCategoryCategoryId(Long categoryId);
    
    // Find all document types by category name
    List<DocumentType> findByCategoryName(String categoryName);
    
 // Method 2: Case-insensitive search
    List<DocumentType> findByCategoryNameIgnoreCase(String categoryName);
    
    // Find document type by name and category
    Optional<DocumentType> findByNameAndCategory(String name, CheckCategory category);
    
    Optional<DocumentType> findByName(String name);
    Optional<DocumentType> findByCode(String code);
   
    List<DocumentType> findByIsRequired(boolean isRequired);
    
    
    
    boolean existsByName(String name);
    boolean existsByCode(String code);
    
    @Query("SELECT dt FROM DocumentType dt WHERE dt.category.categoryId IN :categoryIds")
    List<DocumentType> findByCategoryIds(@Param("categoryIds") List<Long> categoryIds);
    
    
    
}
