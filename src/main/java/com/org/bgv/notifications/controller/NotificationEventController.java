package com.org.bgv.notifications.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.org.bgv.api.response.CustomApiResponse;
import com.org.bgv.notifications.NotificationEvent;
import com.org.bgv.notifications.dto.NotificationContext;
import com.org.bgv.notifications.dto.NotificationEventDTO;
import com.org.bgv.notifications.service.NotificationDispatcherService;
import com.org.bgv.notifications.service.NotificationEventService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/notification-events")
@RequiredArgsConstructor
@Slf4j
public class NotificationEventController {

    private final NotificationDispatcherService dispatcher;
    
    private final NotificationEventService service;
    
    @GetMapping
    public ResponseEntity<CustomApiResponse<List<NotificationEventDTO>>> getEvents() {

        log.info("Fetching active notification events");

        try {
            List<NotificationEventDTO> events = service.getActiveEvents();

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Notification events fetched successfully",
                            events,
                            HttpStatus.OK
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST
                    ));

        } catch (Exception e) {
            log.error("Failed to fetch notification events", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to fetch notification events",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }


    @PostMapping("/trigger/{event}")
    public ResponseEntity<CustomApiResponse<Void>> trigger(
            @PathVariable NotificationEvent event,
            @RequestBody NotificationContext context
    ) {
        log.info("Triggering notification event: {}, context: {}", event, context);

        try {
            dispatcher.dispatch(event, context);

            return ResponseEntity.ok(
                    CustomApiResponse.success(
                            "Notification triggered successfully",
                            null,
                            HttpStatus.OK
                    )
            );

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CustomApiResponse.failure(
                            e.getMessage(),
                            HttpStatus.BAD_REQUEST
                    ));

        } catch (Exception e) {
            log.error("Failed to trigger notification event: {}", event, e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CustomApiResponse.failure(
                            "Failed to trigger notification",
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ));
        }
    }

}
