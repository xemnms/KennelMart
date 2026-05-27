package com.kennel.mart.kennelmart.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "notifications", indexes = {
    @Index(name = "idx_user", columnList = "user_id"),
    @Index(name = "idx_read", columnList = "is_read")
})
public class Notification extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @NotBlank
    @Column(length = 1000)
    private String message;

    @Column(name = "is_read")
    private boolean read = false;

    @Column(name = "type")
    private String type; // e.g., "VERIFICATION", "ORDER", "MESSAGE"

    @Column(name = "reference_id")
    private String referenceId; // optional: order ID, verification ID, etc.

    public Notification() {}

    public Notification(User user, String title, String message, String type, String referenceId) {
        this.user = user;
        this.title = title;
        this.message = message;
        this.type = type;
        this.referenceId = referenceId;
    }

    // Getters and setters
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
}