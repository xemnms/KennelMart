package com.kennel.mart.kennelmart.controller;

import com.kennel.mart.kennelmart.dto.NotificationResponse;
import com.kennel.mart.kennelmart.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<Page<NotificationResponse>> getNotifications(Authentication authentication,
                                                                       Pageable pageable) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(notificationService.getUserNotifications(userEmail, pageable));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnreadCount(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(notificationService.getUnreadCount(userEmail));
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        String userEmail = authentication.getName();
        notificationService.markAllAsRead(userEmail);
        return ResponseEntity.ok().build();
    }
}