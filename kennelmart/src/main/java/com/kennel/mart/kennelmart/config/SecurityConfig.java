package com.kennel.mart.kennelmart.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.kennel.mart.kennelmart.security.JwtAuthenticationFilter;
import com.kennel.mart.kennelmart.security.JwtProvider;

// Lombok removed

/**
 * Spring Security Configuration.
 * 
 * Configures:
 * - JWT authentication
 * - Password encoding (BCrypt)
 * - HTTP security policies
 * - CORS settings
 * - Session management
 * 
 * OOP Principles:
 * - Configuration centralization
 * - Abstraction of security concerns
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
// Lombok removed
public class SecurityConfig {

    private JwtProvider jwtProvider;
    private UserDetailsService userDetailsService;

        public SecurityConfig(JwtProvider jwtProvider, UserDetailsService userDetailsService) {
            this.jwtProvider = jwtProvider;
            this.userDetailsService = userDetailsService;
        }

    /**
     * Password encoder using BCrypt with strength 12.
     * 
     * BCrypt is adaptive and slows down over time to resist brute force attacks.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Authentication manager for authentication operations.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * JWT Authentication Filter.
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtProvider, userDetailsService);
    }

    /**
     * CORS Configuration.
     * 
     * Allows frontend (React) to communicate with backend API.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:3000",
                "http://127.0.0.1:5173",
                "http://127.0.0.1:3000"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * HTTP Security Configuration.
     * 
     * Defines:
     * - Public endpoints (no authentication required)
     * - Protected endpoints (JWT required)
     * - Session management (stateless with JWT)
     * - CORS settings
     * 
     * Authorization Model:
     * - ADMIN: Full system access, including admin endpoints
     * - USER: Standard user, can buy and sell on marketplace
     * All authenticated users (ADMIN and USER) can access marketplace features.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize ->
                        authorize
                                // Public endpoints - no authentication required
                                .requestMatchers("/api/auth/**").permitAll()
                                .requestMatchers("/", "/index.html", "/static/**", "/public/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                // Marketplace endpoints - all authenticated users (ADMIN and USER) can buy and sell
                                .requestMatchers("/api/cart/**", "/api/orders/**", "/api/listings/**").hasAnyRole("ADMIN", "USER")
                                // Admin endpoints - only ADMIN role
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                // Any other request requires authentication
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
