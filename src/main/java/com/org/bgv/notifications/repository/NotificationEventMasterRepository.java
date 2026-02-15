package com.org.bgv.notifications.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.org.bgv.notifications.NotificationEvent;
import com.org.bgv.notifications.entity.NotificationEventMaster;

import java.util.List;

public interface NotificationEventMasterRepository
        extends JpaRepository<NotificationEventMaster, Long> {

    List<NotificationEventMaster> findByIsActiveTrueOrderByCategoryAsc();
    
    List<NotificationEventMaster> findByIsActiveTrue();
    
    boolean existsByEventCode(NotificationEvent eventCode);
}
