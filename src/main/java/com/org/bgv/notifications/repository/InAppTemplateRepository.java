package com.org.bgv.notifications.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.notifications.InAppTemplate;

@Repository
public interface InAppTemplateRepository
        extends JpaRepository<InAppTemplate, Long> {

    
	List<InAppTemplate> findByActive(boolean active);
	
    List<InAppTemplate> findByCompanyId(Long companyId);

    List<InAppTemplate> findByCompanyIdIsNull();
    
    Optional<InAppTemplate> findByTemplateCode(
            String templateCode
            
    );
    
    boolean existsByTemplateCode(String templateCode);

    @Query("""
        select t from InAppTemplate t
        where t.templateCode = :code
          and (t.companyId = :companyId or t.companyId is null)
        order by t.companyId desc
    """)
    Optional<InAppTemplate> findResolvedTemplate(
            @Param("code") String templateCode,
            @Param("companyId") Long companyId
    );
    
    
}

