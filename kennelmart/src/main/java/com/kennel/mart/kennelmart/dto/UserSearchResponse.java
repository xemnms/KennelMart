package com.kennel.mart.kennelmart.dto;

import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.enums.UserRole;
import java.util.UUID;

/**
 * DTO for public user search results.
 * Excludes sensitive fields like password, studentOrFacultyId, accountStatus, verificationStatus.
 */
public class UserSearchResponse {
    private UUID id;
    private String name;
    private String email;
    private String profileImage;
    private Double averageRating;
    private UserRole role;

    public UserSearchResponse() {}

    public UserSearchResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.profileImage = user.getProfileImage();
        this.averageRating = user.getAverageRating();
        this.role = user.getRole();
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}