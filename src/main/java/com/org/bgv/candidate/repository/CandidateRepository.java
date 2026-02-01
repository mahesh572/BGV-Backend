package com.org.bgv.candidate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.candidate.entity.Candidate;


@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long>, JpaSpecificationExecutor<Candidate> {
	
	Optional<Candidate> findByUuid(String uuid);
	
	Optional<Candidate> findByUserUserId(Long userId);
	
	// Check if candidate has any consent records
    boolean existsByCandidateId(Long candidateId);
    
    boolean existsByUserUserIdAndCompanyId(Long userId,Long companyId);
    
    List<Candidate> findByCompanyId(Long companyId);
    
    
    Page<Candidate> findAll(Specification<Candidate> spec, Pageable pageable);
    
    boolean existsByUserUserId(Long userId);
    
    // For search by user details
    List<Candidate> findByUserEmailContainingIgnoreCase(String email);
    
    Candidate findByCompanyIdAndUserUserId(Long companyId,Long userId);
    
    
    @Query("SELECT c FROM Candidate c WHERE c.company.id = :companyId AND c.candidateId = :candidateId")
    Optional<Candidate> findByCompanyIdAndCandidateId(@Param("companyId") Long companyId, 
                                                      @Param("candidateId") Long candidateId);
    
    Optional<Candidate> findByCandidateRef(String candidateRef);
    
    
    Optional<Candidate> findByUserUserIdAndCompanyId(Long userId,Long companyId);
}
