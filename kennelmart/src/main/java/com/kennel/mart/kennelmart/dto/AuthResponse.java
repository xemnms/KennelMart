package com.kennel.mart.kennelmart.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.kennel.mart.kennelmart.enums.AccountStatus;
import com.kennel.mart.kennelmart.enums.UserRole;
import com.kennel.mart.kennelmart.enums.VerificationStatus;

/**
 * DTO for authentication response with JWT token.
 * 
 * Returns user information and JWT token after successful login/registration.
 * Hides sensitive internal fields (password, etc).
 */
// Lombok removed
public class AuthResponse {

    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String profileImage;
    private UserRole role;
    private VerificationStatus verificationStatus;
    private AccountStatus accountStatus;
    private String accessToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private LocalDateTime createdAt;

    public AuthResponse() {}

    public AuthResponse(UUID userId, String firstName, String lastName, String email, String profileImage, UserRole role, VerificationStatus verificationStatus, AccountStatus accountStatus, String accessToken, long expiresIn, LocalDateTime createdAt) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.profileImage = profileImage;
        this.role = role;
        this.verificationStatus = verificationStatus;
        this.accountStatus = accountStatus;
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.createdAt = createdAt;
    }

    public UUID getUserId() { return userId; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getProfileImage() { return profileImage; }
    public UserRole getRole() { return role; }
    public VerificationStatus getVerificationStatus() { return verificationStatus; }
    public AccountStatus getAccountStatus() { return accountStatus; }
    public String getAccessToken() { return accessToken; }
    public String getTokenType() { return tokenType; }
    public long getExpiresIn() { return expiresIn; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setUserId(UUID userId) { this.userId = userId; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setEmail(String email) { this.email = email; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    public void setRole(UserRole role) { this.role = role; }
    public void setVerificationStatus(VerificationStatus verificationStatus) { this.verificationStatus = verificationStatus; }
    public void setAccountStatus(AccountStatus accountStatus) { this.accountStatus = accountStatus; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Manual builder for AuthResponse
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private UUID userId;
        private String firstName;
        private String lastName;
        private String email;
        private String profileImage;
        private UserRole role;
        private VerificationStatus verificationStatus;
        private AccountStatus accountStatus;
        private String accessToken;
        private String tokenType = "Bearer";
        private long expiresIn;
        private LocalDateTime createdAt;

        public Builder userId(UUID userId) { this.userId = userId; return this; }
        public Builder firstName(String firstName) { this.firstName = firstName; return this; }
        public Builder lastName(String lastName) { this.lastName = lastName; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder profileImage(String profileImage) { this.profileImage = profileImage; return this; }
        public Builder role(UserRole role) { this.role = role; return this; }
        public Builder verificationStatus(VerificationStatus verificationStatus) { this.verificationStatus = verificationStatus; return this; }
        public Builder accountStatus(AccountStatus accountStatus) { this.accountStatus = accountStatus; return this; }
        public Builder accessToken(String accessToken) { this.accessToken = accessToken; return this; }
        public Builder tokenType(String tokenType) { this.tokenType = tokenType; return this; }
        public Builder expiresIn(long expiresIn) { this.expiresIn = expiresIn; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public AuthResponse build() {
            AuthResponse ar = new AuthResponse();
            ar.userId = this.userId;
            ar.firstName = this.firstName;
            ar.lastName = this.lastName;
            ar.email = this.email;
            ar.profileImage = this.profileImage;
            ar.role = this.role;
            ar.verificationStatus = this.verificationStatus;
            ar.accountStatus = this.accountStatus;
            ar.accessToken = this.accessToken;
            ar.tokenType = this.tokenType;
            ar.expiresIn = this.expiresIn;
            ar.createdAt = this.createdAt;
            return ar;
        }
    }
}
