package com.kennel.mart.kennelmart.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kennel.mart.kennelmart.dto.AuthResponse;
import com.kennel.mart.kennelmart.dto.LoginRequest;
import com.kennel.mart.kennelmart.dto.RegisterRequest;
import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.enums.AccountStatus;
import com.kennel.mart.kennelmart.enums.UserRole;
import com.kennel.mart.kennelmart.enums.VerificationStatus;
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
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthServiceImpl.class);

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        
        // Normalize email
        String normalizedEmail = request.getEmail().toLowerCase();

        // Validation
        validateRegistrationRequest(request);

        // Check if user already exists
        if (userRepository.existsByEmail(normalizedEmail)) {
            log.warn("User registration failed: email already exists - {}", normalizedEmail);
            throw new IllegalArgumentException("Email is already registered");
        }

        // Determine user role (admin or regular user)
        UserRole role = normalizedEmail.equalsIgnoreCase("bagayam@students.nu-laguna.edu.ph") 
                ? UserRole.ADMIN 
                : UserRole.USER;

        // Admin accounts are automatically verified in this phase
        VerificationStatus vStatus = (role == UserRole.ADMIN) ? VerificationStatus.VERIFIED : VerificationStatus.PENDING;

        // Create new user
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .studentOrFacultyId(request.getStudentOrFacultyId())
                .role(role)  // ADMIN if bagayam, USER otherwise
                .verificationStatus(vStatus)
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {} ({}) with role: {}", savedUser.getId(), savedUser.getEmail(), role);

        // Generate JWT token
        String token = jwtProvider.generateToken(
                new org.springframework.security.core.userdetails.User(
                        savedUser.getEmail(),
                        savedUser.getPassword(),
                        java.util.Collections.singleton(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + savedUser.getRole().toString()))
                )
        );

        return buildAuthResponse(savedUser, token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Authenticating user with email: {}", request.getEmail());

        // Authenticate with Spring Security
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (org.springframework.security.core.AuthenticationException e) {
            log.warn("Login failed for email: {} - {}", request.getEmail(), e.getMessage());
            throw new IllegalArgumentException("Invalid email or password");
        }

        // Get authenticated user
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check account status
        if (!user.isActive()) {
            log.warn("Login attempt for suspended account: {}", user.getEmail());
            throw new IllegalArgumentException("Account has been suspended");
        }

        try {
            // Generate JWT token
            String token = jwtProvider.generateToken(userDetails);

            log.info("User logged in successfully: {}", user.getEmail());
            return buildAuthResponse(user, token);

        } catch (Exception e) {
            log.error("Unexpected error during login for email: {}", request.getEmail(), e);
            throw new RuntimeException("An internal error occurred during login");
        }
    }

    /**
     * Validate registration request data.
     */
    private void validateRegistrationRequest(RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        if (request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        if (!request.getEmail().toLowerCase().endsWith("@students.nu-laguna.edu.ph")) {
            throw new IllegalArgumentException("Only @students.nu-laguna.edu.ph emails are allowed");
        }
    }

    /**
     * Build authentication response DTO from user entity.
     */
    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .role(user.getRole())
                .verificationStatus(user.getVerificationStatus())
                .accountStatus(user.getAccountStatus())
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtProvider.getExpirationTime() / 1000)  // Convert to seconds
                .createdAt(user.getCreatedAt())
                .build();
    }
}
