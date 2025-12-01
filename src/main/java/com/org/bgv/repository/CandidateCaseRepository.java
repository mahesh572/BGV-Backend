package com.org.bgv.repository;

import com.org.bgv.constants.CaseStatus;
import com.org.bgv.entity.CandidateCase;
import com.org.bgv.entity.EmployerPackage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateCaseRepository extends JpaRepository<CandidateCase, Long> {
    
    List<CandidateCase> findByCandidateId(Long candidateId);
    
    List<CandidateCase> findByCompanyId(Long companyId);
    
    List<CandidateCase> findByEmployerPackageId(Long employerPackageId);
    
    List<CandidateCase> findByStatus(CaseStatus status);
    
    List<CandidateCase> findByCompanyIdAndStatus(Long companyId, CaseStatus status);


    List<CandidateCase> findByCompanyIdAndStatusIn(Long companyId, List<CaseStatus> statuses);
    
    
    @Query("SELECT cc FROM CandidateCase cc WHERE cc.candidateId = :candidateId AND cc.status IN :statuses")
    List<CandidateCase> findByCandidateIdAndStatusIn(@Param("candidateId") Long candidateId, 
                                                   @Param("statuses") List<CaseStatus> statuses);
    
    Optional<CandidateCase> findByCandidateIdAndEmployerPackageId(Long candidateId, Long employerPackageId);
    
    @Query("SELECT COUNT(cc) FROM CandidateCase cc WHERE cc.employerPackage.id = :employerPackageId")
    Long countByEmployerPackage(@Param("employerPackageId") Long employerPackageId);
    
    @Query("SELECT COUNT(cc) FROM CandidateCase cc WHERE cc.employerPackage = :employerPackage AND cc.status != 'COMPLETED'")
    Long countByEmployerPackageAndStatusNot(@Param("employerPackage") EmployerPackage employerPackage, 
                                          @Param("status") CaseStatus status);
    
}