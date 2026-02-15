package com.org.bgv.notifications.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.notifications.NotificationEvent;
import com.org.bgv.notifications.entity.NotificationPolicy;

@Repository
public interface NotificationPolicyRepository
        extends JpaRepository<NotificationPolicy, Long> {

	Optional<NotificationPolicy> findByEventAndCompanyId(
            NotificationEvent event,
            Long companyId
    );

    List<NotificationPolicy> findByCompanyId(Long employerId);

    List<NotificationPolicy> findByCompanyIdIsNull();
}

