package com.org.bgv.company.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.company.entity.EmployerPackageCheckCategory;

public interface EmployerPackageCheckCategoryRepository
extends JpaRepository<EmployerPackageCheckCategory, Long> {

// 🔹 Get all categories for a given employer package
List<EmployerPackageCheckCategory> findByEmployerPackage_Id(Long employerPackageId);

// 🔹 Get specific category inside employer package
Optional<EmployerPackageCheckCategory> findByEmployerPackage_IdAndCategory_CategoryId(
    Long employerPackageId,
    Long categoryId
);

// 🔹 Delete all categories when package is removed/unassigned
void deleteByEmployerPackage_Id(Long employerPackageId);

// 🔹 Check if category already exists (useful to prevent duplicates)
boolean existsByEmployerPackage_IdAndCategory_CategoryId(
    Long employerPackageId,
    Long categoryId
);
}
