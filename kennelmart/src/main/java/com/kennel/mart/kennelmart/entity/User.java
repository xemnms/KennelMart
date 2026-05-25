package com.kennel.mart.kennelmart.entity;

import com.kennel.mart.kennelmart.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
// Lombok removed: explicit constructors, getters, setters, builder

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
    @Index(name = "idx_idnumber", columnList = "idnumber")
})
public class User extends BaseEntity {
    @NotBlank(message = "Name is required")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Idnumber is required")
    @Column(name = "idnumber", nullable = false)
    private String idnumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    public User() {}

    public User(String name, String idnumber, String email, UserRole role) {
        this.name = name;
        this.idnumber = idnumber;
        this.email = email;
        this.role = role;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIdnumber() { return idnumber; }
    public void setIdnumber(String idnumber) { this.idnumber = idnumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", idnumber='" + idnumber + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                '}';
    }
}
