package com.org.bgv.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.entity.Document;

public interface DocumentRepository extends JpaRepository<Document, Long> {
	
	List<Document> findByProfile_ProfileId(Long profileId);
	
	
}