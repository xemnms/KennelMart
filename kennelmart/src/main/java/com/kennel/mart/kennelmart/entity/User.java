package com.kennel.mart.kennelmart.entity;

import com.kennel.mart.kennelmart.enums.AccountStatus;
import com.kennel.mart.kennelmart.enums.UserRole;
import com.kennel.mart.kennelmart.enums.VerificationStatus;

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
    @Index(name = "idx_student_faculty_id", columnList = "student_or_faculty_id")
})
// Lombok removed: explicit constructors, getters, setters, builder
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

    public User() {}

    public User(String firstName, String lastName, String email, String password, String studentOrFacultyId, String profileImage, UserRole role, VerificationStatus verificationStatus, AccountStatus accountStatus) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.studentOrFacultyId = studentOrFacultyId;
        this.profileImage = profileImage;
        this.role = role;
        this.verificationStatus = verificationStatus;
        this.accountStatus = accountStatus;
    }

    // Getters and setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getStudentOrFacultyId() { return studentOrFacultyId; }
    public void setStudentOrFacultyId(String studentOrFacultyId) { this.studentOrFacultyId = studentOrFacultyId; }
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
    public VerificationStatus getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(VerificationStatus verificationStatus) { this.verificationStatus = verificationStatus; }
    public AccountStatus getAccountStatus() { return accountStatus; }
    public void setAccountStatus(AccountStatus accountStatus) { this.accountStatus = accountStatus; }

    // Builder pattern for User
    public static UserBuilder builder() { return new UserBuilder(); }
    public static class UserBuilder {
        private String firstName, lastName, email, password, studentOrFacultyId, profileImage;
        private UserRole role;
        private VerificationStatus verificationStatus;
        private AccountStatus accountStatus;
        private java.util.UUID id;
            public UserBuilder id(java.util.UUID id) { this.id = id; return this; }
        public UserBuilder firstName(String firstName) { this.firstName = firstName; return this; }
        public UserBuilder lastName(String lastName) { this.lastName = lastName; return this; }
        public UserBuilder email(String email) { this.email = email; return this; }
        public UserBuilder password(String password) { this.password = password; return this; }
        public UserBuilder studentOrFacultyId(String studentOrFacultyId) { this.studentOrFacultyId = studentOrFacultyId; return this; }
        public UserBuilder profileImage(String profileImage) { this.profileImage = profileImage; return this; }
        public UserBuilder role(UserRole role) { this.role = role; return this; }
        public UserBuilder verificationStatus(VerificationStatus verificationStatus) { this.verificationStatus = verificationStatus; return this; }
        public UserBuilder accountStatus(AccountStatus accountStatus) { this.accountStatus = accountStatus; return this; }
        public User build() {
            User user = new User(firstName, lastName, email, password, studentOrFacultyId, profileImage, role, verificationStatus, accountStatus);
            if (id != null) {
                try {
                    java.lang.reflect.Field idField = User.class.getSuperclass().getDeclaredField("id");
                    idField.setAccessible(true);
                    idField.set(user, id);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to set id on User", e);
                }
            }
            return user;
        }
    }

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

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", studentOrFacultyId='" + studentOrFacultyId + '\'' +
                ", profileImage='" + profileImage + '\'' +
                ", role=" + role +
                ", verificationStatus=" + verificationStatus +
                ", accountStatus=" + accountStatus +
                '}';
    }

    /**
     * Check if user account is active.
     */
    public boolean isActive() {
        return accountStatus == AccountStatus.ACTIVE;
    }
}
