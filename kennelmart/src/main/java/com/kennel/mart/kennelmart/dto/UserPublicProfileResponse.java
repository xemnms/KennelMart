package com.kennel.mart.kennelmart.dto;

import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.enums.UserRole;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserPublicProfileResponse {
    private UUID id;
    private String name;
    private String profileImage;
    private Double averageRating;
    private UserRole role;
    private LocalDateTime joinedAt;

    public UserPublicProfileResponse() {}

    public UserPublicProfileResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.profileImage = user.getProfileImage();
        this.averageRating = user.getAverageRating();
        this.role = user.getRole();
        this.joinedAt = user.getCreatedAt(); // BaseEntity provides createdAt
    }

    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
}