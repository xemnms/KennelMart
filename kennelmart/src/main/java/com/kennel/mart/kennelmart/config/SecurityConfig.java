package com.kennel.mart.kennelmart.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtProvider jwtProvider, UserDetailsService userDetailsService) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtProvider, userDetailsService);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

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
                                .requestMatchers("/error", "/error/**").permitAll()
                                .requestMatchers("/", "/index.html", "/static/**", "/public/**").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                                .requestMatchers("/api/verification/status").permitAll()

                                // Verification endpoints are available to authenticated users (including unverified)
                                .requestMatchers("/api/verification/**").authenticated()

                                // Serve uploaded images (static files)
                                .requestMatchers("/uploads/**").permitAll()

                                // Public GET endpoints for browsing listings
                                .requestMatchers(HttpMethod.GET, "/api/listings", "/api/listings/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/categories").permitAll()

                                // Public GET for user search (marketplace)
                                .requestMatchers(HttpMethod.GET, "/api/users/search").permitAll()

                                // Public GET for user public profile
                                .requestMatchers(HttpMethod.GET, "/api/users/{userId}/public").permitAll()

                                // Public GET for seller listings (used in user profile page)
                                .requestMatchers(HttpMethod.GET, "/api/listings/users/{userId}/listings").permitAll()

                                // Public GET for reviews (average and list)
                                .requestMatchers(HttpMethod.GET, "/api/reviews/sellers/**").permitAll()

                                // Verified users (or admins) can access core transactional features
                                .requestMatchers(HttpMethod.POST, "/api/uploads/image").hasAnyAuthority("ROLE_ADMIN", "VERIFIED_USER")

                                // Listing management - verified users/admins only
                                .requestMatchers(HttpMethod.POST, "/api/listings").hasAnyAuthority("ROLE_ADMIN", "VERIFIED_USER")
                                .requestMatchers(HttpMethod.PUT, "/api/listings/**").hasAnyAuthority("ROLE_ADMIN", "VERIFIED_USER")
                                .requestMatchers(HttpMethod.DELETE, "/api/listings/**").hasAnyAuthority("ROLE_ADMIN", "VERIFIED_USER")
                                .requestMatchers(HttpMethod.GET, "/api/listings/my-listings").hasAnyAuthority("ROLE_ADMIN", "VERIFIED_USER")

                                // Cart and orders - verified users/admins only
                                .requestMatchers("/api/cart/**").hasAnyAuthority("ROLE_ADMIN", "VERIFIED_USER")
                                .requestMatchers("/api/orders/**").hasAnyAuthority("ROLE_ADMIN", "VERIFIED_USER")

                                // Reviews - submit requires verified account/admin
                                .requestMatchers(HttpMethod.POST, "/api/reviews").hasAnyAuthority("ROLE_ADMIN", "VERIFIED_USER")
                                .requestMatchers("/api/reviews/admin/**").hasRole("ADMIN")

                                // Reports - submit requires verified account/admin
                                .requestMatchers(HttpMethod.POST, "/api/reports").hasAnyAuthority("ROLE_ADMIN", "VERIFIED_USER")

                                // Messaging endpoints - verified users/admins only
                                .requestMatchers(HttpMethod.POST, "/api/messages").hasAnyAuthority("ROLE_ADMIN", "VERIFIED_USER")
                                .requestMatchers(HttpMethod.GET, "/api/messages/**").hasAnyAuthority("ROLE_ADMIN", "VERIFIED_USER")
                                .requestMatchers(HttpMethod.PUT, "/api/messages/**").hasAnyAuthority("ROLE_ADMIN", "VERIFIED_USER")

                                // Notifications - verified users/admins only
                                .requestMatchers("/api/notifications/**").hasAnyAuthority("ROLE_ADMIN", "VERIFIED_USER")

                                // Analytics - verified users/admins only
                                .requestMatchers("/api/analytics/**").hasAnyAuthority("ROLE_ADMIN", "VERIFIED_USER")

                                // User profile endpoints (except public search and public profile)
                                .requestMatchers(HttpMethod.GET, "/api/users/me", "/api/users/me/authorities").authenticated()
                                .requestMatchers("/api/users/me/**").authenticated()
                                // Other /api/users/** endpoints (except those already permitted) require authentication
                                .requestMatchers("/api/users/**").authenticated()

                                // Admin endpoints - only ADMIN role
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                                // Any other request requires authentication
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}