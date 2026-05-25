package com.kennel.mart.kennelmart.service;

import com.kennel.mart.kennelmart.dto.AuthResponse;
import com.kennel.mart.kennelmart.dto.LoginRequest;
import com.kennel.mart.kennelmart.dto.RegisterRequest;

/**
 * Service interface for authentication operations.
 * 
 * Defines contract for authentication use cases:
 * - User registration
 * - User login
 * - User info retrieval
 * 
 * Demonstrates OOP principle: Abstraction via interface
 */
public interface AuthService {

    /**
     * Register a new user with email and password.
     * 
     * Business Rules:
     * - Email must be unique
     * - Password must match confirmPassword
     * - Password must be at least 8 characters
     * - NU email required
     * 
     * @param request user registration data
     * @return authentication response with JWT token
     * @throws IllegalArgumentException if validation fails
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticate user with email and password.
     * 
     * @param request user login credentials
     * @return authentication response with JWT token
     * @throws IllegalArgumentException if credentials are invalid
     */
    AuthResponse login(LoginRequest request);
}
