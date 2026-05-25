package com.kennel.mart.kennelmart.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kennel.mart.kennelmart.dto.AuthResponse;
import com.kennel.mart.kennelmart.dto.LoginRequest;
import com.kennel.mart.kennelmart.dto.RegisterRequest;
import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.enums.UserRole;
import com.kennel.mart.kennelmart.repository.UserRepository;
import com.kennel.mart.kennelmart.security.JwtProvider;
import com.kennel.mart.kennelmart.service.AuthService;

/**
 * Implementation of Authentication Service.
 * 
 * Handles user registration and login with JWT token generation.
 * 
 * OOP Principles:
 * - Single Responsibility: Authentication operations only
 * - Dependency Injection: Constructor-based
 * - Transactional Safety: Database consistency
 */
@Service
// Lombok removed
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthServiceImpl.class);

    public AuthServiceImpl(UserRepository userRepository, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
        public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        String normalizedEmail = request.getEmail().toLowerCase();
        validateRegistrationRequest(request);
        if (userRepository.existsByEmail(normalizedEmail)) {
            log.warn("User registration failed: email already exists - {}", normalizedEmail);
            throw new IllegalArgumentException("Email is already registered");
        }
        UserRole role = normalizedEmail.equals("bagayam@students.nu-laguna.edu.ph") ? UserRole.ADMIN : UserRole.USER;
        User user = new User(request.getName(), request.getSchoolId(), normalizedEmail, role);
        User savedUser = userRepository.save(user);
        String token = jwtProvider.generateToken(
            new org.springframework.security.core.userdetails.User(
                savedUser.getEmail(),
                "", // No password
                java.util.Collections.singleton(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + savedUser.getRole().toString()))
            )
        );
        return buildAuthResponse(savedUser, token);
        }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Authenticating user with email: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        // For demo: no password check, just email match
        String token = jwtProvider.generateToken(
                new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        "", // No password
                        java.util.Collections.singleton(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole().toString()))
                )
        );
        log.info("User logged in successfully: {}", user.getEmail());
        return buildAuthResponse(user, token);
    }

    /**
     * Validate registration request data.
     */
    private void validateRegistrationRequest(RegisterRequest request) {
        if (!request.getEmail().toLowerCase().endsWith("@students.nu-laguna.edu.ph")) {
            throw new IllegalArgumentException("Only @students.nu-laguna.edu.ph emails are allowed");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (request.getSchoolId() == null || request.getSchoolId().trim().isEmpty()) {
            throw new IllegalArgumentException("Idnumber is required");
        }
    }

    /**
     * Build authentication response DTO from user entity.
     */
    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
            .userId(user.getId())
            .name(user.getName())
            .idnumber(user.getIdnumber())
            .email(user.getEmail())
            .role(user.getRole())
            .accessToken(token)
            .tokenType("Bearer")
            .expiresIn(jwtProvider.getExpirationTime() / 1000)
            .createdAt(user.getCreatedAt())
            .build();
    }
}
