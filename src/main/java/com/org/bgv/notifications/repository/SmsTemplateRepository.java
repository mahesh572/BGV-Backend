package com.org.bgv.notifications.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.org.bgv.notifications.entity.SmsTemplate;

import lombok.Data;


@Repository
public interface SmsTemplateRepository
        extends JpaRepository<SmsTemplate, Long> {

    @Query("""
        SELECT t FROM SmsTemplate t
        WHERE t.templateCode = :code
          AND (t.companyId = :companyId OR t.companyId IS NULL)
        ORDER BY t.companyId DESC
    """)
    SmsTemplate findResolvedTemplate(
            String code,
            Long companyId
    );

    List<SmsTemplate> findByCompanyId(Long companyId);

    List<SmsTemplate> findByCompanyIdIsNull();
    
    Optional<SmsTemplate> findByTemplateCodeAndCompanyId(
            String templateCode,
            Long companyId
    );
}
