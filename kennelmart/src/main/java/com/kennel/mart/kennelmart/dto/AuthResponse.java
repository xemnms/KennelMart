package com.kennel.mart.kennelmart.dto;

import com.kennel.mart.kennelmart.enums.AccountStatus;
import com.kennel.mart.kennelmart.enums.UserRole;
import com.kennel.mart.kennelmart.enums.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for authentication response with JWT token.
 * 
 * Returns user information and JWT token after successful login/registration.
 * Hides sensitive internal fields (password, etc).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
