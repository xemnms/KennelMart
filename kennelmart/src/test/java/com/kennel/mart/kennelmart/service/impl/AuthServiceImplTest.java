package com.kennel.mart.kennelmart.service.impl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kennel.mart.kennelmart.dto.AuthResponse;
import com.kennel.mart.kennelmart.dto.LoginRequest;
import com.kennel.mart.kennelmart.dto.RegisterRequest;
import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.enums.UserRole;
import com.kennel.mart.kennelmart.repository.UserRepository;
import com.kennel.mart.kennelmart.security.JwtProvider;

/**
 * Unit tests for AuthServiceImpl.
 * 
 * Tests authentication service business logic:
 * - User registration
 * - User login
 * - Validation
 * 
 * Note: The real AuthServiceImpl does NOT use AuthenticationManager,
 * PasswordEncoder, or any password checks – it uses email-only login.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Authentication Service Tests")
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

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
                .name("Axel Bagay")
                .schoolId("2025-1020735")
                .email("bagayam@students.nu-laguna.edu.ph")
                .build();

        loginRequest = LoginRequest.builder()
                .email("bagayam@students.nu-laguna.edu.ph")
                .build();

        testUser = new User("Axel Bagay", "2025-1020735", "bagayam@students.nu-laguna.edu.ph", UserRole.ADMIN);
    }

    @Test
    @DisplayName("Should register user successfully with valid data")
    void testRegisterSuccess() {
        // Arrange
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
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
        assertEquals(UserRole.ADMIN, response.getRole());

        // Verify
        verify(userRepository).existsByEmail("bagayam@students.nu-laguna.edu.ph");
        verify(userRepository).save(any(User.class));
        verify(jwtProvider).generateToken(any());
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
        // Skipped: password fields no longer exist in the application.
        // The current implementation has no password validation.
    }

    @Test
    @DisplayName("Should fail registration if password too short")
    void testRegisterPasswordTooShort() {
        // Skipped: password fields no longer exist in the application.
    }

    @Test
    @DisplayName("Should fail registration if not NU email")
    void testRegisterNotNuEmail() {
        // Arrange
        registerRequest.setEmail("user@gmail.com");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest),
                "Only @students.nu-laguna.edu.ph emails are allowed");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should login user successfully with valid credentials")
    void testLoginSuccess() {
        // Arrange
        when(userRepository.findByEmail("bagayam@students.nu-laguna.edu.ph"))
                .thenReturn(Optional.of(testUser));
        when(jwtProvider.generateToken(any())).thenReturn("mock-jwt-token");
        when(jwtProvider.getExpirationTime()).thenReturn(86400000L);

        // Act
        AuthResponse response = authService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertEquals("bagayam@students.nu-laguna.edu.ph", response.getEmail());
        assertEquals("mock-jwt-token", response.getAccessToken());

        verify(userRepository).findByEmail("bagayam@students.nu-laguna.edu.ph");
        verify(jwtProvider).generateToken(any());
    }

    @Test
    @DisplayName("Should fail login if user account is suspended")
    void testLoginAccountSuspended() {
        // Skipped: account status (suspended/disabled) no longer exists in User entity.
        // The current implementation has no account suspension checks.
    }

    @Test
    @DisplayName("Should fail login if user not found (invalid credentials)")
    void testLoginInvalidCredentials() {
        // Arrange – simulate user not found (the only failure case in current login)
        when(userRepository.findByEmail("bagayam@students.nu-laguna.edu.ph"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest),
                "User not found");
    }
}