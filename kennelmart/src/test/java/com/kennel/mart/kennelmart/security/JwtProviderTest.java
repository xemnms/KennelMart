package com.kennel.mart.kennelmart.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JwtProvider.
 * 
 * Tests JWT token generation and validation.
 */
@DisplayName("JWT Provider Tests")
class JwtProviderTest {

    private JwtProvider jwtProvider;
    private final String testSecret = "test-secret-key-that-is-long-enough-for-testing-purposes-256-bits";
    private final long testExpiration = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider();
        ReflectionTestUtils.setField(jwtProvider, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtProvider, "jwtExpirationMs", testExpiration);
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void testGenerateTokenSuccess() {
        // Arrange
        UserDetails userDetails = new User("bagayam@students.nu-laguna.edu.ph", "password", Collections.emptySet());

        // Act
        String token = jwtProvider.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains("."));
    }

    @Test
    @DisplayName("Should extract username from token")
    void testGetUsernameFromToken() {
        // Arrange
        UserDetails userDetails = new User("bagayam@students.nu-laguna.edu.ph", "password", Collections.emptySet());
        String token = jwtProvider.generateToken(userDetails);

        // Act
        String username = jwtProvider.getUsernameFromToken(token);

        // Assert
        assertEquals("bagayam@students.nu-laguna.edu.ph", username);
    }

    @Test
    @DisplayName("Should validate correct token")
    void testValidateTokenSuccess() {
        // Arrange
        UserDetails userDetails = new User("bagayam@students.nu-laguna.edu.ph", "password", Collections.emptySet());
        String token = jwtProvider.generateToken(userDetails);

        // Act
        boolean isValid = jwtProvider.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should reject invalid token")
    void testValidateTokenInvalid() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isValid = jwtProvider.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should check if token is expired")
    void testIsTokenExpired() {
        // Arrange
        jwtProvider = new JwtProvider();
        ReflectionTestUtils.setField(jwtProvider, "jwtSecret", testSecret);
        ReflectionTestUtils.setField(jwtProvider, "jwtExpirationMs", 1L); // 1ms expiration
        UserDetails userDetails = new User("bagayam@students.nu-laguna.edu.ph", "password", Collections.emptySet());
        String token = jwtProvider.generateToken(userDetails);

        // Wait a bit to ensure token expires
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act
        boolean isExpired = jwtProvider.isTokenExpired(token);

        // Assert
        assertTrue(isExpired);
    }

    @Test
    @DisplayName("Should get expiration time")
    void testGetExpirationTime() {
        // Act
        long expiration = jwtProvider.getExpirationTime();

        // Assert
        assertEquals(testExpiration, expiration);
    }

    @Test
    @DisplayName("Should extract claims from token")
    void testGetClaimsFromToken() {
        // Arrange
        UserDetails userDetails = new User("bagayam@students.nu-laguna.edu.ph", "password", Collections.emptySet());
        String token = jwtProvider.generateToken(userDetails);

        // Act
        Claims claims = jwtProvider.getClaimsFromToken(token);

        // Assert
        assertNotNull(claims);
        assertEquals("bagayam@students.nu-laguna.edu.ph", claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());
    }
}
