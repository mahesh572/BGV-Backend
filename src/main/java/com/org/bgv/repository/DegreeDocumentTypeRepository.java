package com.org.bgv.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.org.bgv.entity.DegreeDocumentType;

public interface DegreeDocumentTypeRepository 
        extends JpaRepository<DegreeDocumentType, Long> {

    List<DegreeDocumentType> 
        findByDegreeType_DegreeIdAndActiveTrue(Long degreeId);

}
