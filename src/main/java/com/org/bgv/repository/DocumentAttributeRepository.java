package com.org.bgv.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.entity.DocumentAttribute;

@Repository
public interface DocumentAttributeRepository extends JpaRepository<DocumentAttribute, Long> {

    // Find attribute by system code (FRONT, BACK, PHOTO, SIGNATURE)
    Optional<DocumentAttribute> findByCode(String code);

    // Safety checks (optional but recommended)
    boolean existsByCode(String code);
}
