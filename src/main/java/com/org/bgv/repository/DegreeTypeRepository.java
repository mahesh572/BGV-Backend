package com.org.bgv.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.entity.DegreeType;

public interface DegreeTypeRepository extends JpaRepository<DegreeType, Long> {
	
	Optional<DegreeType> findByName(String name);
	
    List<DegreeType> findAll();
    
        
    List<DegreeType> findByNameContainingIgnoreCase(String name);
    
    // If you want to get by label as well
    List<DegreeType> findByLabelContainingIgnoreCase(String label);
}