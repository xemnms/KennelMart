package com.kennel.mart.kennelmart.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConversationDTO {
    private UUID userId;
    private String name;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private int unreadCount;

    // Getters and setters
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLastMessage() { return lastMessage; }
    public void setLastMessage(String lastMessage) { this.lastMessage = lastMessage; }
    public LocalDateTime getLastMessageTime() { return lastMessageTime; }
    public void setLastMessageTime(LocalDateTime lastMessageTime) { this.lastMessageTime = lastMessageTime; }
    public int getUnreadCount() { return unreadCount; }
    public void setUnreadCount(int unreadCount) { this.unreadCount = unreadCount; }
}