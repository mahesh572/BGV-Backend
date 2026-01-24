package com.org.bgv.notifications.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.org.bgv.notifications.dto.NotificationEventDTO;
import com.org.bgv.notifications.entity.NotificationEventMaster;
import com.org.bgv.notifications.repository.NotificationEventMasterRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationEventService {

    private final NotificationEventMasterRepository eventRepository;

    /**
     * 🔹 Get all active notification events (Master Data)
     */
    public List<NotificationEventDTO> getAllEvents() {
        log.info("Fetching all notification events");

        return eventRepository.findByIsActiveTrueOrderByCategoryAsc()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    
    public List<NotificationEventDTO> getActiveEvents() {
        return eventRepository.findByIsActiveTrue()
                .stream()
                .map(e -> NotificationEventDTO.builder()
                		.id(e.getId())
                        .eventCode(e.getEventCode())
                        .displayName(e.getDisplayName())
                        .category(e.getCategory())
                        .description(e.getDescription())
                        .severity(e.getSeverity())
                        .build())
                .toList();
    }

    /**
     * 🔹 Map Entity → DTO
     */
    private NotificationEventDTO mapToDto(NotificationEventMaster event) {
        return NotificationEventDTO.builder()
                .id(event.getId())
                .eventCode(event.getEventCode())
                .displayName(event.getDisplayName())
                .category(event.getCategory())
                .description(event.getDescription())
                .severity(event.getSeverity())
                .supportedChannels(event.getSupportedChannels())
                .build();
    }
}
