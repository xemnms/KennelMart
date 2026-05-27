package com.kennel.mart.kennelmart.controller;

import com.kennel.mart.kennelmart.dto.MessageRequest;
import com.kennel.mart.kennelmart.dto.MessageResponse;
import com.kennel.mart.kennelmart.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody MessageRequest request,
                                                       Authentication authentication) {
        String senderEmail = authentication.getName();
        MessageResponse response = messageService.sendMessage(senderEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/conversation/{userId}")
    public ResponseEntity<Page<MessageResponse>> getConversation(@PathVariable UUID userId,
                                                                 Pageable pageable,
                                                                 Authentication authentication) {
        String currentUserEmail = authentication.getName();
        Page<MessageResponse> messages = messageService.getConversation(currentUserEmail, userId, pageable);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnreadCount(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(messageService.getUnreadCount(userEmail));
    }

    @PutMapping("/read/{userId}")
    public ResponseEntity<Void> markMessagesAsRead(@PathVariable UUID userId,
                                                   Authentication authentication) {
        String currentUserEmail = authentication.getName();
        messageService.markMessagesAsRead(currentUserEmail, userId);
        return ResponseEntity.ok().build();
    }
}