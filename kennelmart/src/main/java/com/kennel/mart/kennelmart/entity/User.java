package com.kennel.mart.kennelmart.entity;

import com.kennel.mart.kennelmart.enums.AccountStatus;
import com.kennel.mart.kennelmart.enums.UserRole;
import com.kennel.mart.kennelmart.enums.VerificationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User entity representing all platform users (buyers, sellers, admins).
 * 
 * Core user information and account status for authentication and authorization.
 * 
 * OOP Principles Demonstrated:
 * - Encapsulation: Private fields with getters/setters
 * - Composition: User has role and verification status
 * - Single Responsibility: Manages user core data only
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_email", columnList = "email", unique = true),
    @Index(name = "idx_student_faculty_id", columnList = "student_or_faculty_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "student_or_faculty_id")
    private String studentOrFacultyId;

    @Column(name = "profile_image")
    private String profileImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false)
    private VerificationStatus verificationStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", nullable = false)
    private AccountStatus accountStatus;

    /**
     * Helper method to get user's full name.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     * Check if user is verified.
     */
    public boolean isVerified() {
        return verificationStatus == VerificationStatus.VERIFIED;
    }

    /**
     * Check if user account is active.
     */
    public boolean isActive() {
        return accountStatus == AccountStatus.ACTIVE;
    }
}
