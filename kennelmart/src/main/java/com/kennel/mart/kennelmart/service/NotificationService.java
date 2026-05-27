package com.kennel.mart.kennelmart.service;

import com.kennel.mart.kennelmart.dto.NotificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface NotificationService {
    void sendNotification(UUID userId, String title, String message, String type, String referenceId);
    Page<NotificationResponse> getUserNotifications(String userEmail, Pageable pageable);
    int getUnreadCount(String userEmail);
    void markAllAsRead(String userEmail);
}