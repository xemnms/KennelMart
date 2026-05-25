package com.kennel.mart.kennelmart.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO for submitting identity verification request.
 */
public class VerificationRequest {

    @NotBlank(message = "Student/Faculty ID is required")
    private String studentOrFacultyId;

    // Optional: for MVP, admin will manually check ID; you can add image URL later
    private String idImageUrl;

    public VerificationRequest() {}

    public VerificationRequest(String studentOrFacultyId, String idImageUrl) {
        this.studentOrFacultyId = studentOrFacultyId;
        this.idImageUrl = idImageUrl;
    }

    public String getStudentOrFacultyId() { return studentOrFacultyId; }
    public void setStudentOrFacultyId(String studentOrFacultyId) { this.studentOrFacultyId = studentOrFacultyId; }

    public String getIdImageUrl() { return idImageUrl; }
    public void setIdImageUrl(String idImageUrl) { this.idImageUrl = idImageUrl; }
}