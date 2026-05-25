package com.kennel.mart.kennelmart.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

/**
 * VerifiedIdentity entity represents a pre‑approved or manually verified
 * student/faculty identity from National University Laguna.
 * 
 * Admins can pre‑load this table or add entries when verifying users.
 * During registration, a user's studentOrFacultyId is checked against this table
 * (or verification status is set to PENDING and admin manually approves).
 * 
 * OOP Principles:
 * - Encapsulation: private fields with public getters/setters
 * - Single Responsibility: manages verified identities only
 */
@Entity
@Table(name = "verified_identities", indexes = {
    @Index(name = "idx_school_id", columnList = "schoolId", unique = true),
    @Index(name = "idx_email", columnList = "email", unique = true)
})
public class VerifiedIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(name = "school_id", nullable = false, unique = true)
    private String schoolId;          // e.g., "2025-1020735"

    @NotBlank
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    // Default constructor (required by JPA)
    public VerifiedIdentity() {}

    // Convenience constructor
    public VerifiedIdentity(String schoolId, String fullName, String email) {
        this.schoolId = schoolId;
        this.fullName = fullName;
        this.email = email;
    }

    // Getters and Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "VerifiedIdentity{" +
                "id=" + id +
                ", schoolId='" + schoolId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}