package com.org.bgv.notifications.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.notifications.dto.InAppNotificationDTO;
import com.org.bgv.notifications.service.InAppNotificationQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class InAppNotificationController {

    private final InAppNotificationQueryService service;

    // 🔔 Bell / Inbox
    @GetMapping
    public List<InAppNotificationDTO> getAll(
            @RequestParam Long userId
    ) {
        return service.getAll(userId);
    }

    // 🔴 Unread count
    @GetMapping("/unread")
    public List<InAppNotificationDTO> getUnread(
            @RequestParam Long userId
    ) {
        return service.getUnread(userId);
    }

    // ✅ Mark one as read
    @PostMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        service.markAsRead(id);
    }

    // ✅ Mark all as read
    @PostMapping("/read-all")
    public void markAll(@RequestParam Long userId) {
        service.markAllAsRead(userId);
    }
}
