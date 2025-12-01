package com.org.bgv.repository;

import com.org.bgv.constants.EmployerPackageStatus;
import com.org.bgv.entity.EmployerPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployerPackageRepository extends JpaRepository<EmployerPackage, Long> {
    
    List<EmployerPackage> findByCompanyId(Long companyId);  // Changed from employerId to companyId
    
    List<EmployerPackage> findByCompanyIdAndStatus(Long companyId, EmployerPackageStatus status);
    
    Optional<EmployerPackage> findByIdAndCompanyId(Long id, Long companyId);
    
    @Query("SELECT ep FROM EmployerPackage ep WHERE ep.companyId = :companyId AND ep.bgvPackage.packageId = :packageId AND ep.status = 'ACTIVE'")
    Optional<EmployerPackage> findActiveByCompanyAndPackage(@Param("companyId") Long companyId, 
                                                          @Param("packageId") Long packageId);
    
    boolean existsByCompanyIdAndBgvPackage_PackageIdAndStatus(Long companyId, Long packageId, EmployerPackageStatus status);
    
   
    

}