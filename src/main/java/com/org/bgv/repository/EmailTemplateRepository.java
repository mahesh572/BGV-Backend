package com.org.bgv.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.EmailTemplate;

@Repository
public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {
    Optional<EmailTemplate> findByType(String type);
    List<EmailTemplate> findByIsActiveTrue();
    boolean existsByType(String type);
    
    @Query("SELECT COUNT(e) > 0 FROM EmailTemplate e WHERE e.type = :type AND e.id != :id")
    boolean existsByTypeAndIdNot(@Param("type") String type, @Param("id") Long id);
    
    @Query("SELECT e FROM EmailTemplate e WHERE e.isActive = true ORDER BY e.name ASC")
    List<EmailTemplate> findAllActiveOrderByName();
}