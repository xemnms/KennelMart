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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

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
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Authentication Service Tests")
class AuthServiceImplTest {

    static {
        // Workaround for Byte Buddy incompatibility with Java 25
        System.setProperty("net.bytebuddy.experimental", "true");
    }

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
        // Skipped: password fields no longer exist
    }

    @Test
    @DisplayName("Should fail registration if password too short")
    void testRegisterPasswordTooShort() {
        // Arrange
        // Skipped: password fields no longer exist
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
        // Skipped: account status no longer exists
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