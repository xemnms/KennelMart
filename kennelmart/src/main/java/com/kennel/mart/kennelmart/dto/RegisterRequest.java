package com.kennel.mart.kennelmart.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// Lombok removed: explicit constructors, getters, setters, builder

/**
 * DTO for user registration request.
 * 
 * Receives registration data from frontend.
 * Validates all required fields before processing.
 * 
 * Never expose entity directly - always use DTOs for API contracts.
 */
// Lombok removed: explicit constructors, getters, setters, builder
public class RegisterRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @NotBlank(message = "Student/Faculty ID is required")
    private String studentOrFacultyId;

    public RegisterRequest() {}
    public RegisterRequest(String firstName, String lastName, String email, String password, String confirmPassword, String studentOrFacultyId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.studentOrFacultyId = studentOrFacultyId;
    }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public String getStudentOrFacultyId() { return studentOrFacultyId; }
    public void setStudentOrFacultyId(String studentOrFacultyId) { this.studentOrFacultyId = studentOrFacultyId; }

    // Builder pattern
    public static RegisterRequestBuilder builder() { return new RegisterRequestBuilder(); }
    public static class RegisterRequestBuilder {
        private String firstName, lastName, email, password, confirmPassword, studentOrFacultyId;
        public RegisterRequestBuilder firstName(String firstName) { this.firstName = firstName; return this; }
        public RegisterRequestBuilder lastName(String lastName) { this.lastName = lastName; return this; }
        public RegisterRequestBuilder email(String email) { this.email = email; return this; }
        public RegisterRequestBuilder password(String password) { this.password = password; return this; }
        public RegisterRequestBuilder confirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; return this; }
        public RegisterRequestBuilder studentOrFacultyId(String studentOrFacultyId) { this.studentOrFacultyId = studentOrFacultyId; return this; }
        public RegisterRequest build() {
            return new RegisterRequest(firstName, lastName, email, password, confirmPassword, studentOrFacultyId);
        }
    }
    @Override
    public String toString() {
        return "RegisterRequest{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", studentOrFacultyId='" + studentOrFacultyId + '\'' +
                '}';
    }
}
