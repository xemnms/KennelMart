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
                                .requestMatchers("/api/verification/status").permitAll()
                                
                                // Public GET endpoints for browsing listings
                                .requestMatchers(HttpMethod.GET, "/api/listings", "/api/listings/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/categories").permitAll()
                                
                                // Public GET for reviews (average and list)
                                .requestMatchers(HttpMethod.GET, "/api/reviews/sellers/**").permitAll()
                                
                                // Listing management - require authentication
                                .requestMatchers(HttpMethod.POST, "/api/listings").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/api/listings/**").authenticated()
                                .requestMatchers(HttpMethod.DELETE, "/api/listings/**").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/listings/my-listings").authenticated()
                                
                                // Cart and Orders - require authentication
                                .requestMatchers("/api/cart/**").authenticated()
                                .requestMatchers("/api/orders/**").authenticated()
                                
                                // Reviews - submit requires authentication, admin only for admin endpoints
                                .requestMatchers(HttpMethod.POST, "/api/reviews").authenticated()
                                .requestMatchers("/api/reviews/admin/**").hasRole("ADMIN")
                                
                                // Messaging endpoints - all require authentication
                                .requestMatchers(HttpMethod.POST, "/api/messages").authenticated()
                                .requestMatchers(HttpMethod.GET, "/api/messages/**").authenticated()
                                .requestMatchers(HttpMethod.PUT, "/api/messages/**").authenticated()
                                
                                // Notifications - require authentication
                                .requestMatchers("/api/notifications/**").authenticated()
                                
                                // Analytics - require authentication
                                .requestMatchers("/api/analytics/**").authenticated()
                                
                                // Admin endpoints - only ADMIN role
                                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                                
                                // Any other request requires authentication
                                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}