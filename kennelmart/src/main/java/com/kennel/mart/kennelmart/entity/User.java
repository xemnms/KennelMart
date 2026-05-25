package com.kennel.mart.kennelmart.entity;

import com.kennel.mart.kennelmart.enums.AccountStatus;
import com.kennel.mart.kennelmart.enums.UserRole;
import com.kennel.mart.kennelmart.enums.VerificationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * User entity representing all platform users (buyers, sellers, admins).
 * 
 * Core user information for authentication, authorization, and identity verification.
 * 
 * OOP Principles:
 * - Encapsulation: private fields with public getters/setters
 * - Single Responsibility: manages user data and account state only
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_email", columnList = "email", unique = true),
    @Index(name = "idx_student_or_faculty_id", columnList = "studentOrFacultyId")
})
public class User extends BaseEntity {

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(nullable = false)
    private String password;   // stored as BCrypt hash

    @Column(name = "student_or_faculty_id", unique = true)
    private String studentOrFacultyId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    @Column(length = 500)
    private String profileImage;

    // Average rating for sellers (Phase 7)
    @Column(name = "average_rating")
    private Double averageRating;

    // Default constructor (required by JPA)
    public User() {}

    // Convenience constructor for essential fields
    public User(String name, String email, String password, String studentOrFacultyId, UserRole role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.studentOrFacultyId = studentOrFacultyId;
        this.role = role;
        this.verificationStatus = VerificationStatus.PENDING;
        this.accountStatus = AccountStatus.ACTIVE;
    }

    // Getters and Setters

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStudentOrFacultyId() {
        return studentOrFacultyId;
    }

    public void setStudentOrFacultyId(String studentOrFacultyId) {
        this.studentOrFacultyId = studentOrFacultyId;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", verificationStatus=" + verificationStatus +
                ", accountStatus=" + accountStatus +
                ", averageRating=" + averageRating +
                '}';
    }
}