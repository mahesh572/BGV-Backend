package com.org.bgv.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.BgvPackage;

@Repository
public interface PackageRepository extends JpaRepository<BgvPackage, Long> {
    
    Optional<BgvPackage> findByCode(String code);
    
    List<BgvPackage> findByIsActiveTrue();
    
    List<BgvPackage> findByCustomizableTrue();
    
    boolean existsByCode(String code);
}
