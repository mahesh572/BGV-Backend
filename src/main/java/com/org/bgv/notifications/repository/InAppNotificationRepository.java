package com.org.bgv.notifications.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.notifications.InAppNotification;

@Repository
public interface InAppNotificationRepository
        extends JpaRepository<InAppNotification, Long> {

    List<InAppNotification> findByRecipientUserIdOrderByCreatedAtDesc(
            Long recipientUserId
    );

    List<InAppNotification> findByRecipientUserIdAndReadFalseOrderByCreatedAtDesc(
            Long recipientUserId
    );

    @Modifying
    @Query("""
        UPDATE InAppNotification n
        SET n.read = true
        WHERE n.id = :id
    """)
    void markAsRead(@Param("id") Long id);

    @Modifying
    @Query("""
        UPDATE InAppNotification n
        SET n.read = true
        WHERE n.recipientUserId = :userId
    """)
    void markAllAsRead(@Param("userId") Long userId);
}
