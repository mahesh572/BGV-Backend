package com.org.bgv.notifications.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.notifications.NotificationEvent;
import com.org.bgv.notifications.dto.NotificationContext;
import com.org.bgv.notifications.dto.NotificationEventDTO;
import com.org.bgv.notifications.service.NotificationDispatcherService;
import com.org.bgv.notifications.service.NotificationEventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/notification-events")
@RequiredArgsConstructor
public class NotificationEventController {

    private final NotificationDispatcherService dispatcher;
    
    private final NotificationEventService service;
    
    @GetMapping
    public List<NotificationEventDTO> getEvents() {
        return service.getActiveEvents();
    }

    @PostMapping("/trigger/{event}")
    public void trigger(
            @PathVariable NotificationEvent event,
            @RequestBody NotificationContext context
    ) {
        dispatcher.dispatch(event, context);
    }
}
