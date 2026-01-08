package com.org.bgv.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.CheckCategory;
import com.org.bgv.entity.RuleTypes;

@Repository
public interface RuleTypesRepository extends JpaRepository<RuleTypes, Long> {
    
    List<RuleTypes> findByCategory(CheckCategory category);
    
    List<RuleTypes> findByCategoryCategoryId(Long categoryId);
    

    
    Optional<RuleTypes> findByCode(String code);
    
    List<RuleTypes> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT r FROM RuleTypes r WHERE r.category.categoryId = :categoryId AND r.name = :name")
    Optional<RuleTypes> findByCategoryAndName(@Param("categoryId") Long categoryId, @Param("name") String name);
    
    boolean existsByCode(String code);
    
    boolean existsByCodeAndCategoryCategoryId(String code, Long categoryId);
    
    // Additional useful methods
    boolean existsByNameAndCategoryCategoryId(String name, Long categoryId);
    
}