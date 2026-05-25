package com.kennel.mart.kennelmart.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
// Lombok removed

/**
 * JWT Token Provider for generating and validating JWT tokens.
 * 
 * Handles all JWT operations:
 * - Token generation with expiration
 * - Token validation
 * - Claims extraction
 * 
 * Security Best Practice:
 * - Secret key is environment-based
 * - Token expiration is configurable
 * - HS512 algorithm with strong key
 */
@Component
// Lombok removed
public class JwtProvider {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JwtProvider.class);

    @Value("${jwt.secret:your-secret-key-change-this-in-production-at-least-256-bits-long}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")  // Default: 24 hours in milliseconds
    private long jwtExpirationMs;

    /**
     * Generate JWT token for authenticated user.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Generate JWT token with additional claims.
     */
    public String generateToken(UserDetails userDetails, Map<String, Object> claims) {
        if (claims == null) {
            claims = new HashMap<>();
        }
        claims.put("username", userDetails.getUsername());
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Create JWT token with claims.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extract username (email) from token.
     */
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * Extract all claims from token.
     */
    public Claims getClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Check if token is valid.
     */
    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Check if token is expired.
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    /**
     * Get token expiration time in milliseconds.
     */
    public long getExpirationTime() {
        return jwtExpirationMs;
    }
}
