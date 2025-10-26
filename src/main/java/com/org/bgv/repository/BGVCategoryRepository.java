package com.org.bgv.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.BGVCategory;

@Repository
public interface BGVCategoryRepository extends JpaRepository<BGVCategory, Long>{
	Optional<BGVCategory> findByName(String name);
	List<BGVCategory> findByIsActiveTrue();
}
