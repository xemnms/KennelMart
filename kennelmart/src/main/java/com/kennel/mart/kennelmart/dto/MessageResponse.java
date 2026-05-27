package com.kennel.mart.kennelmart.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class MessageResponse {
    private UUID id;
    private UUID senderId;
    private String senderName;
    private UUID receiverId;
    private String receiverName;
    private String content;
    private boolean read;
    private LocalDateTime createdAt;

    // Builder
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private MessageResponse response = new MessageResponse();
        public Builder id(UUID id) { response.id = id; return this; }
        public Builder senderId(UUID senderId) { response.senderId = senderId; return this; }
        public Builder senderName(String senderName) { response.senderName = senderName; return this; }
        public Builder receiverId(UUID receiverId) { response.receiverId = receiverId; return this; }
        public Builder receiverName(String receiverName) { response.receiverName = receiverName; return this; }
        public Builder content(String content) { response.content = content; return this; }
        public Builder read(boolean read) { response.read = read; return this; }
        public Builder createdAt(LocalDateTime createdAt) { response.createdAt = createdAt; return this; }
        public MessageResponse build() { return response; }
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public UUID getReceiverId() { return receiverId; }
    public String getReceiverName() { return receiverName; }
    public String getContent() { return content; }
    public boolean isRead() { return read; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}