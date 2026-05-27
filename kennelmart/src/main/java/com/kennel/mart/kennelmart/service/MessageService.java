package com.kennel.mart.kennelmart.service;

import com.kennel.mart.kennelmart.dto.MessageRequest;
import com.kennel.mart.kennelmart.dto.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface MessageService {
    MessageResponse sendMessage(String senderEmail, MessageRequest request);
    Page<MessageResponse> getConversation(String userEmail, UUID otherUserId, Pageable pageable);
    int getUnreadCount(String userEmail);
    void markMessagesAsRead(String userEmail, UUID otherUserId);
}