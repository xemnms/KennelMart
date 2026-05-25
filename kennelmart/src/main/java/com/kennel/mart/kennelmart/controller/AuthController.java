package com.kennel.mart.kennelmart.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kennel.mart.kennelmart.dto.AuthResponse;
import com.kennel.mart.kennelmart.dto.LoginRequest;
import com.kennel.mart.kennelmart.dto.RegisterRequest;
import com.kennel.mart.kennelmart.service.AuthService;

import jakarta.validation.Valid;

/**
 * REST Controller for authentication endpoints.
 * 
 * Handles user registration and login operations.
 * 
 * Endpoints:
 * - POST /api/auth/register - Register new user
 * - POST /api/auth/login - Login with credentials
 * - GET /api/auth/me - Get current authenticated user
 * 
 * OOP Principles:
 * - Single Responsibility: Authentication endpoints only
 * - Validation: Request body validation
 * - Error Handling: Proper HTTP status codes
 */
@RestController
@RequestMapping("/api/auth")
// Lombok removed
public class AuthController {

    private AuthService authService;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register a new user.
     * 
     * POST /api/auth/register
     * 
     * Request body:
     * {
     *   "firstName": "Axel",
     *   "lastName": "Bagay",
     *   "email": "bagayam@students.nu-laguna.edu.ph",
     *   "password": "SecurePass123",
     *   "confirmPassword": "SecurePass123",
     *   "studentOrFacultyId": "2025-1020735"
     * }
     * 
     * Validation Rules:
     * - Email must be NU Laguna email (@students.nu-laguna.edu.ph)
     * - Password minimum 8 characters
     * - Passwords must match
     * - Email must be unique
     * 
     * @param request registration request data
     * @return 201 Created with authentication response and JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration request received for email: {}", request.getEmail());
        try {
            AuthResponse response = authService.register(request);
            log.info("Registration successful for user: {}", response.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("Registration failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Login user with credentials.
     * 
     * POST /api/auth/login
     * 
     * Request body:
     * {
     *   "email": "bagayam@students.nu-laguna.edu.ph",
     *   "password": "SecurePass123"
     * }
     * 
     * @param request login request data
     * @return 200 OK with authentication response and JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for email: {}", request.getEmail());
        try {
            AuthResponse response = authService.login(request);
            log.info("Login successful for user: {}", response.getEmail());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.warn("Login failed: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Get current authenticated user information.
     * 
     * GET /api/auth/me
     * 
     * Requires: Valid JWT token in Authorization header
     * 
     * @return 200 OK with current user information
     */
    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            log.info("Retrieved current user: {}", username);
            return ResponseEntity.ok("Current user: " + username);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authenticated user found");
    }
}
