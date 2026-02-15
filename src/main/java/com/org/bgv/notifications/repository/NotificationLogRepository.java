package com.org.bgv.notifications.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.org.bgv.notifications.NotificationChannel;
import com.org.bgv.notifications.NotificationEvent;
import com.org.bgv.notifications.NotificationLog;

@Repository
public interface NotificationLogRepository
        extends JpaRepository<NotificationLog, Long> {

    List<NotificationLog> findByCompanyId(Long companyId);

    List<NotificationLog> findByEvent(NotificationEvent event);

    List<NotificationLog> findByChannel(NotificationChannel channel);

    List<NotificationLog> findBySuccessFalse();
}

