package com.org.bgv.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.entity.CheckType;

public interface CheckTypeRepository extends JpaRepository<CheckType, Long>{
	Optional<CheckType> findByName(String name);
	List<CheckType> findByIsActiveTrue();
    List<CheckType> findByCategoryCategoryIdAndIsActiveTrue(Long categoryId);
}
