package com.kennel.mart.kennelmart.service.impl;

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

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AuthServiceImpl.class);

    public AuthServiceImpl(UserRepository userRepository, JwtProvider jwtProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
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

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        UserRole role = normalizedEmail.equals("bagayam@students.nu-laguna.edu.ph") ? UserRole.ADMIN : UserRole.USER;
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
            request.getName(),
            normalizedEmail,
            encodedPassword,
            request.getStudentOrFacultyId(),
            role
        );

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        String token = generateJwtToken(savedUser);
        return buildAuthResponse(savedUser, token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Authenticating user with email: {}", request.getEmail());
        String normalizedEmail = request.getEmail().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (user.getAccountStatus() != AccountStatus.ACTIVE) {
            log.warn("Login attempt for suspended account: {}", normalizedEmail);
            throw new IllegalArgumentException("Account is suspended. Please contact support.");
        }

        if (user.getVerificationStatus() != VerificationStatus.VERIFIED) {
            log.warn("Login attempt for unverified account: {}", normalizedEmail);
            throw new IllegalArgumentException("Account not verified. Please submit your ID for verification.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Invalid password attempt for email: {}", normalizedEmail);
            throw new IllegalArgumentException("Invalid email or password");
        }

        log.info("User logged in successfully: {}", user.getEmail());
        String token = generateJwtToken(user);
        return buildAuthResponse(user, token);
    }

    private void validateRegistrationRequest(RegisterRequest request) {
        if (!request.getEmail().toLowerCase().endsWith("@students.nu-laguna.edu.ph")) {
            throw new IllegalArgumentException("Only @students.nu-laguna.edu.ph emails are allowed");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (request.getStudentOrFacultyId() == null || request.getStudentOrFacultyId().trim().isEmpty()) {
            throw new IllegalArgumentException("Student/Faculty ID is required");
        }
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
    }

    private String generateJwtToken(User user) {
        return jwtProvider.generateToken(
            new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                java.util.Collections.singleton(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole().toString()))
            )
        );
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
            .userId(user.getId())
            .name(user.getName())
            .idnumber(user.getStudentOrFacultyId())
            .email(user.getEmail())
            .role(user.getRole())
            .accessToken(token)
            .tokenType("Bearer")
            .expiresIn(jwtProvider.getExpirationTime() / 1000)
            .createdAt(user.getCreatedAt())
            .build();
    }
}