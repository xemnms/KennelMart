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
import org.springframework.security.crypto.password.PasswordEncoder;

import com.kennel.mart.kennelmart.dto.AuthResponse;
import com.kennel.mart.kennelmart.dto.LoginRequest;
import com.kennel.mart.kennelmart.dto.RegisterRequest;
import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.enums.AccountStatus;
import com.kennel.mart.kennelmart.enums.UserRole;
import com.kennel.mart.kennelmart.enums.VerificationStatus;
import com.kennel.mart.kennelmart.repository.UserRepository;
import com.kennel.mart.kennelmart.security.JwtProvider;

@ExtendWith(MockitoExtension.class)
@DisplayName("Authentication Service Tests")
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

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
                .studentOrFacultyId("2025-1020735")
                .email("bagayam@students.nu-laguna.edu.ph")
                .password("SecurePass123")
                .confirmPassword("SecurePass123")
                .build();

        loginRequest = LoginRequest.builder()
                .email("bagayam@students.nu-laguna.edu.ph")
                .password("SecurePass123")
                .build();

        testUser = new User(
                "Axel Bagay",
                "bagayam@students.nu-laguna.edu.ph",
                "encodedPasswordHash",
                "2025-1020735",
                UserRole.ADMIN
        );
        testUser.setVerificationStatus(VerificationStatus.VERIFIED);
        testUser.setAccountStatus(AccountStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should register user successfully with valid data")
    void testRegisterSuccess() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPasswordHash");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtProvider.generateToken(any())).thenReturn("mock-jwt-token");
        when(jwtProvider.getExpirationTime()).thenReturn(86400000L);

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("bagayam@students.nu-laguna.edu.ph", response.getEmail());
        assertEquals("mock-jwt-token", response.getAccessToken());
        assertEquals(UserRole.ADMIN, response.getRole());

        verify(userRepository).existsByEmail("bagayam@students.nu-laguna.edu.ph");
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder).encode("SecurePass123");
        verify(jwtProvider).generateToken(any());
    }

    @Test
    @DisplayName("Should fail registration if email already exists")
    void testRegisterEmailAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest),
                "Email is already registered");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should fail registration if passwords do not match")
    void testRegisterPasswordsDoNotMatch() {
        registerRequest.setConfirmPassword("DifferentPass");
        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest),
                "Passwords do not match");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should fail registration if password too short")
    void testRegisterPasswordTooShort() {
        registerRequest.setPassword("short");
        registerRequest.setConfirmPassword("short");
        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest),
                "Password must be at least 8 characters");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should fail registration if not NU email")
    void testRegisterNotNuEmail() {
        registerRequest.setEmail("user@gmail.com");
        assertThrows(IllegalArgumentException.class, () -> authService.register(registerRequest),
                "Only @students.nu-laguna.edu.ph emails are allowed");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should login user successfully with valid credentials")
    void testLoginSuccess() {
        when(userRepository.findByEmail("bagayam@students.nu-laguna.edu.ph"))
                .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("SecurePass123", testUser.getPassword())).thenReturn(true);
        when(jwtProvider.generateToken(any())).thenReturn("mock-jwt-token");
        when(jwtProvider.getExpirationTime()).thenReturn(86400000L);

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("bagayam@students.nu-laguna.edu.ph", response.getEmail());
        verify(userRepository).findByEmail("bagayam@students.nu-laguna.edu.ph");
        verify(passwordEncoder).matches("SecurePass123", testUser.getPassword());
        verify(jwtProvider).generateToken(any());
    }

    @Test
    @DisplayName("Should fail login if user not found")
    void testLoginInvalidCredentials() {
        when(userRepository.findByEmail("bagayam@students.nu-laguna.edu.ph"))
                .thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest),
                "Invalid email or password");
    }

    @Test
    @DisplayName("Should fail login if account is suspended")
    void testLoginAccountSuspended() {
        testUser.setAccountStatus(AccountStatus.SUSPENDED);
        when(userRepository.findByEmail("bagayam@students.nu-laguna.edu.ph"))
                .thenReturn(Optional.of(testUser));
        assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest),
                "Account is suspended. Please contact support.");
    }

    @Test
    @DisplayName("Should fail login if account not verified")
    void testLoginNotVerified() {
        testUser.setVerificationStatus(VerificationStatus.PENDING);
        when(userRepository.findByEmail("bagayam@students.nu-laguna.edu.ph"))
                .thenReturn(Optional.of(testUser));
        assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest),
                "Account not verified. Please submit your ID for verification.");
    }
}