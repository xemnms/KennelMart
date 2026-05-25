package com.kennel.mart.kennelmart.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Idnumber is required")
    private String schoolId;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    public RegisterRequest() {}
    public RegisterRequest(String name, String schoolId, String email) {
        this.name = name;
        this.schoolId = schoolId;
        this.email = email;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSchoolId() { return schoolId; }
    public void setSchoolId(String schoolId) { this.schoolId = schoolId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // Builder pattern
    public static RegisterRequestBuilder builder() { return new RegisterRequestBuilder(); }
    public static class RegisterRequestBuilder {
        private String name, schoolId, email;
        public RegisterRequestBuilder name(String name) { this.name = name; return this; }
        public RegisterRequestBuilder schoolId(String schoolId) { this.schoolId = schoolId; return this; }
        public RegisterRequestBuilder email(String email) { this.email = email; return this; }
        public RegisterRequest build() {
            return new RegisterRequest(name, schoolId, email);
        }
    }
    @Override
    public String toString() {
        return "RegisterRequest{" +
                "name='" + name + '\'' +
                ", schoolId='" + schoolId + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
