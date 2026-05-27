package com.kennel.mart.kennelmart.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationResponse {
    private UUID id;
    private String title;
    private String message;
    private boolean read;
    private String type;
    private String referenceId;
    private LocalDateTime createdAt;

    // Builder
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private NotificationResponse response = new NotificationResponse();
        public Builder id(UUID id) { response.id = id; return this; }
        public Builder title(String title) { response.title = title; return this; }
        public Builder message(String message) { response.message = message; return this; }
        public Builder read(boolean read) { response.read = read; return this; }
        public Builder type(String type) { response.type = type; return this; }
        public Builder referenceId(String referenceId) { response.referenceId = referenceId; return this; }
        public Builder createdAt(LocalDateTime createdAt) { response.createdAt = createdAt; return this; }
        public NotificationResponse build() { return response; }
    }

    // Getters
    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public boolean isRead() { return read; }
    public String getType() { return type; }
    public String getReferenceId() { return referenceId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}