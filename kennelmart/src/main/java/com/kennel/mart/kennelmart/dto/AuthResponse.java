package com.kennel.mart.kennelmart.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.kennel.mart.kennelmart.enums.UserRole;

/**
 * DTO for authentication response with JWT token.
 * 
 * Returns user information and JWT token after successful login/registration.
 * Hides sensitive internal fields (password, etc).
 */
// Lombok removed
public class AuthResponse {
    private UUID userId;
    private String name;
    private String idnumber;
    private String email;
    private UserRole role;
    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private LocalDateTime createdAt;

    public AuthResponse() {}
    public AuthResponse(UUID userId, String name, String idnumber, String email, UserRole role, String accessToken, long expiresIn, LocalDateTime createdAt) {
        this.userId = userId;
        this.name = name;
        this.idnumber = idnumber;
        this.email = email;
        this.role = role;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.createdAt = createdAt;
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIdnumber() { return idnumber; }
    public void setIdnumber(String idnumber) { this.idnumber = idnumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Manual builder for AuthResponse
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private UUID userId;
        private String name;
        private String idnumber;
        private String email;
        private UserRole role;
        private String accessToken;
        private String tokenType = "Bearer";
        private long expiresIn;
        private LocalDateTime createdAt;

        public Builder userId(UUID userId) { this.userId = userId; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder idnumber(String idnumber) { this.idnumber = idnumber; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder role(UserRole role) { this.role = role; return this; }
        public Builder accessToken(String accessToken) { this.accessToken = accessToken; return this; }
        public Builder tokenType(String tokenType) { this.tokenType = tokenType; return this; }
        public Builder expiresIn(long expiresIn) { this.expiresIn = expiresIn; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public AuthResponse build() {
            AuthResponse ar = new AuthResponse();
            ar.userId = this.userId;
            ar.name = this.name;
            ar.idnumber = this.idnumber;
            ar.email = this.email;
            ar.role = this.role;
            ar.accessToken = this.accessToken;
            ar.tokenType = this.tokenType;
            ar.expiresIn = this.expiresIn;
            ar.createdAt = this.createdAt;
            return ar;
        }
    }
}
