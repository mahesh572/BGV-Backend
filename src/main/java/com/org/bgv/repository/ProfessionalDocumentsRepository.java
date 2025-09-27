package com.org.bgv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.entity.Document;
import com.org.bgv.entity.ProfessionalDocuments;
import com.org.bgv.entity.Profile;

import java.util.List;


public interface ProfessionalDocumentsRepository extends JpaRepository<ProfessionalDocuments, Long>{
	List<ProfessionalDocuments> findByProfile_ProfileId(Long profileId);
	// Corrected method name - use objectId instead of object_ididIn
    List<ProfessionalDocuments> findByProfile_ProfileIdAndObjectIdIn(Long profileId, List<Long> objectIds);
    void deleteByProfile_ProfileId(Long profileId); 
}
