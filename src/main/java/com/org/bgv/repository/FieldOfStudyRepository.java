package com.org.bgv.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.org.bgv.entity.FieldOfStudy;

public interface FieldOfStudyRepository extends JpaRepository<FieldOfStudy, Long> {
	 Optional<FieldOfStudy> findByName(String name);
	 List<FieldOfStudy> findAll();
	
}