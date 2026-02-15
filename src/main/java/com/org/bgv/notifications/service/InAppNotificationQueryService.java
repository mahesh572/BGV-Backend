package com.org.bgv.notifications.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.org.bgv.notifications.InAppNotification;
import com.org.bgv.notifications.dto.InAppNotificationDTO;
import com.org.bgv.notifications.repository.InAppNotificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InAppNotificationQueryService {

    private final InAppNotificationRepository repository;

    
    public List<InAppNotificationDTO> getAll(Long userId) {

        return repository
                .findByRecipientUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    
    public List<InAppNotificationDTO> getUnread(Long userId) {

        return repository
                .findByRecipientUserIdAndReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }

   
    @Transactional
    public void markAsRead(Long notificationId) {

        repository.markAsRead(notificationId);
    }

   
    @Transactional
    public void markAllAsRead(Long userId) {

        repository.markAllAsRead(userId);
    }

    // -------------------------
    // Entity → DTO mapping
    // -------------------------
    private InAppNotificationDTO toDto(InAppNotification entity) {

        InAppNotificationDTO dto = new InAppNotificationDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setMessage(entity.getMessage());
        dto.setDeepLink(entity.getDeepLink());
        dto.setPriority(entity.getPriority());
        dto.setRead(entity.isRead());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }
}

