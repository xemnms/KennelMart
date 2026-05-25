package com.kennel.mart.kennelmart.service.impl;

import com.kennel.mart.kennelmart.dto.AuthResponse;
import com.kennel.mart.kennelmart.dto.LoginRequest;
import com.kennel.mart.kennelmart.dto.RegisterRequest;
import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.enums.AccountStatus;
import com.kennel.mart.kennelmart.enums.UserRole;
import com.kennel.mart.kennelmart.enums.VerificationStatus;
import com.kennel.mart.kennelmart.repository.UserRepository;
import com.kennel.mart.kennelmart.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthServiceImpl.
 * 
 * Tests authentication service business logic:
 * - User registration
 * - User login
 * - Validation
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Authentication Service Tests")
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .firstName("Axel")
                .lastName("Bagay")
                .email("bagayam@students.nu-laguna.edu.ph")
                .password("Password@123")
                .confirmPassword("Password@123")
                .studentOrFacultyId("2025-1020735")
                .build();

        loginRequest = LoginRequest.builder()
                .email("bagayam@students.nu-laguna.edu.ph")
                .password("Password@123")
                .build();

        testUser = User.builder()
                .id(UUID.randomUUID())
                .firstName("Axel")
                .lastName("Bagay")
                .email("bagayam@students.nu-laguna.edu.ph")
                .password("hashedPassword")
                .studentOrFacultyId("2025-1020735")
                .role(UserRole.ADMIN)  // Admin role for developer account
                .verificationStatus(VerificationStatus.PENDING)
                .accountStatus(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should register user successfully with valid data")
    void testRegisterSuccess() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtProvider.generateToken(any())).thenReturn("mock-jwt-token");
        when(jwtProvider.getExpirationTime()).thenReturn(86400000L);

        // Act
        AuthResponse response = authService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertEquals("bagayam@students.nu-laguna.edu.ph", response.getEmail());
        assertEquals("mock-jwt-token", response.getAccessToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(UserRole.ADMIN, response.getRole());  // Axel is ADMIN

        // Verify
        verify(userRepository).existsByEmail("bagayam@students.nu-laguna.edu.ph");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should fail registration if email already exists")
    void testRegisterEmailAlreadyExists() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest),
                "Email is already registered");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should fail registration if passwords do not match")
    void testRegisterPasswordsDoNotMatch() {
        // Arrange
        registerRequest.setConfirmPassword("DifferentPassword@123");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest),
                "Passwords do not match");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should fail registration if password too short")
    void testRegisterPasswordTooShort() {
        // Arrange
        registerRequest.setPassword("short");
        registerRequest.setConfirmPassword("short");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest),
                "Password must be at least 8 characters");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should fail registration if not NU email")
    void testRegisterNotNuEmail() {
        // Arrange
        registerRequest.setEmail("user@gmail.com");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest),
                "Only NU.edu.ph emails are allowed");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should login user successfully with valid credentials")
    void testLoginSuccess() {
        // Arrange
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(
                new org.springframework.security.core.userdetails.User(
                        "bagayam@students.nu-laguna.edu.ph",
                        "hashedPassword",
                        java.util.Collections.emptySet()
                )
        );
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(jwtProvider.generateToken(any())).thenReturn("mock-jwt-token");
        when(jwtProvider.getExpirationTime()).thenReturn(86400000L);

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("bagayam@students.nu-laguna.edu.ph", response.getEmail());
        assertEquals("mock-jwt-token", response.getAccessToken());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    @DisplayName("Should fail login if user account is suspended")
    void testLoginAccountSuspended() {
        // Arrange
        testUser.setAccountStatus(AccountStatus.SUSPENDED);
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(
                new org.springframework.security.core.userdetails.User(
                        "john.doe@nu.edu.ph",
                        "hashedPassword",
                        java.util.Collections.emptySet()
                )
        );
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest),
                "Account has been suspended");
    }

    @Test
    @DisplayName("Should fail login if invalid credentials")
    void testLoginInvalidCredentials() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.core.AuthenticationException("Invalid credentials") {});

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest),
                "Invalid email or password");
    }
}
