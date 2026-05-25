package com.kennel.mart.kennelmart.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for user registration request.
 * 
 * Now includes password and student/faculty ID as required by the project spec.
 */
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Student/Faculty ID is required")
    private String studentOrFacultyId;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    // Default constructor
    public RegisterRequest() {}

    // Constructor with all fields
    public RegisterRequest(String name, String studentOrFacultyId, String email, String password, String confirmPassword) {
        this.name = name;
        this.studentOrFacultyId = studentOrFacultyId;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStudentOrFacultyId() { return studentOrFacultyId; }
    public void setStudentOrFacultyId(String studentOrFacultyId) { this.studentOrFacultyId = studentOrFacultyId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    // Builder pattern
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String name, studentOrFacultyId, email, password, confirmPassword;
        public Builder name(String name) { this.name = name; return this; }
        public Builder studentOrFacultyId(String id) { this.studentOrFacultyId = id; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder confirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; return this; }
        public RegisterRequest build() {
            return new RegisterRequest(name, studentOrFacultyId, email, password, confirmPassword);
        }
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "name='" + name + '\'' +
                ", studentOrFacultyId='" + studentOrFacultyId + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}