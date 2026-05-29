package com.org.bgv.notifications.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.enums.NotificationStatus;
import com.org.bgv.notifications.InAppNotification;
import com.org.bgv.notifications.dto.InAppNotificationDTO;
import com.org.bgv.notifications.repository.InAppNotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InAppNotificationQueryService {

    private final InAppNotificationRepository repository;

    // =========================
    // GET ALL NOTIFICATIONS
    // =========================

    public List<InAppNotificationDTO> getAll(Long userId) {

        return repository
                .findByRecipientUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // =========================
    // GET UNREAD NOTIFICATIONS
    // =========================

    public List<InAppNotificationDTO> getUnread(Long userId) {

        return repository
                .findByRecipientUserIdAndStatusOrderByCreatedAtDesc(
                        userId,
                        NotificationStatus.UNREAD
                )
                .stream()
                .map(this::toDto)
                .toList();
    }

    // =========================
    // MARK SINGLE AS READ
    // =========================

    @Transactional
    public void markAsRead(Long notificationId) {

        repository.updateNotificationStatus(
                notificationId,
                NotificationStatus.READ,
                Instant.now()
        );
    }

    // =========================
    // MARK ALL AS READ
    // =========================

    @Transactional
    public void markAllAsRead(Long userId) {

        repository.markAllAsRead(
                userId,
                NotificationStatus.READ,
                Instant.now()
        );
    }

    // =========================
    // GET UNREAD COUNT
    // =========================

    public long getUnreadCount(Long userId) {

        return repository.countByRecipientUserIdAndStatus(
                userId,
                NotificationStatus.UNREAD
        );
    }

    // =========================
    // ENTITY → DTO
    // =========================

    private InAppNotificationDTO toDto(InAppNotification entity) {

        InAppNotificationDTO dto = new InAppNotificationDTO();

        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setMessage(entity.getMessage());
        dto.setDeepLink(entity.getDeepLink());
        dto.setPriority(entity.getPriority());
        dto.setStatus(entity.getStatus());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }
}