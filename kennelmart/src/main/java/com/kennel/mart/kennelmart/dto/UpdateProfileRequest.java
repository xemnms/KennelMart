package com.kennel.mart.kennelmart.dto;

public class UpdateProfileRequest {
    private String name;
    private String studentOrFacultyId;
    private String profileImage;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getStudentOrFacultyId() { return studentOrFacultyId; }
    public void setStudentOrFacultyId(String studentOrFacultyId) { this.studentOrFacultyId = studentOrFacultyId; }
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
}