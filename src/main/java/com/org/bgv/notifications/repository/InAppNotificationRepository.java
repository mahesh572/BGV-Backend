package com.org.bgv.notifications.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.org.bgv.enums.NotificationStatus;
import com.org.bgv.notifications.InAppNotification;

@Repository
public interface InAppNotificationRepository
        extends JpaRepository<InAppNotification, Long> {

    // ==================== FETCH NOTIFICATIONS ====================

    List<InAppNotification> findByRecipientUserIdOrderByCreatedAtDesc(
            Long recipientUserId
    );

    List<InAppNotification>
    findByRecipientUserIdAndStatusOrderByCreatedAtDesc(
            Long recipientUserId,
            NotificationStatus status
    );

    // ==================== MARK SINGLE NOTIFICATION AS READ ====================

    @Modifying
    @Query("""
        UPDATE InAppNotification n
        SET n.status = :status,
            n.readAt = :readAt
        WHERE n.id = :id
    """)
    void updateNotificationStatus(
            @Param("id") Long id,
            @Param("status") NotificationStatus status,
            @Param("readAt") Instant readAt
    );

    // ==================== MARK ALL NOTIFICATIONS AS READ ====================

    @Modifying
    @Query("""
        UPDATE InAppNotification n
        SET n.status = :status,
            n.readAt = :readAt
        WHERE n.recipientUserId = :userId
          AND n.status = 'UNREAD'
    """)
    void markAllAsRead(
            @Param("userId") Long userId,
            @Param("status") NotificationStatus status,
            @Param("readAt") Instant readAt
    );

    // ==================== UNREAD COUNT ====================

    long countByRecipientUserIdAndStatus(
            Long recipientUserId,
            NotificationStatus status
    );

    // ==================== CHECK DUPLICATE EVENT ====================

    boolean existsByEventId(String eventId);

    // ==================== FETCH BY ENTITY ====================

    List<InAppNotification>
    findByEntityIdOrderByCreatedAtDesc(String entityId);
}