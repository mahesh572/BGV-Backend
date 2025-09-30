package com.org.bgv.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.entity.DocumentCategory;

public interface DocumentCategoryRepository extends JpaRepository<DocumentCategory, Long> {
	
	Optional<DocumentCategory> findByName(String name);
	
    Optional<DocumentCategory> findByNameIgnoreCase(String name);
    
    
    Optional<DocumentCategory> findByNameContainingIgnoreCase(String name);
}