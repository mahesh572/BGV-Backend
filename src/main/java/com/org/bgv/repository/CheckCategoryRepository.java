package com.org.bgv.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.org.bgv.entity.CheckCategory;

public interface CheckCategoryRepository extends JpaRepository<CheckCategory, Long> {
	
	Optional<CheckCategory> findByName(String name);
	
    Optional<CheckCategory> findByNameIgnoreCase(String name);
    
    
    Optional<CheckCategory> findByNameContainingIgnoreCase(String name);
    
    
    CheckCategory findByCode(String code);
    boolean existsByName(String name);
    boolean existsByCode(String code);
    
    CheckCategory findByCategoryId(Long categoryId);
    
    
}