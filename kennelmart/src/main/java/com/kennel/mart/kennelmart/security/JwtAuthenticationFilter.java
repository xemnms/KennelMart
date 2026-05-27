package com.kennel.mart.kennelmart.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtProvider jwtProvider, UserDetailsService userDetailsService) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        log.info("Processing request: {}", requestURI);

        // Skip JWT authentication for public auth endpoints (registration and login)
        if (requestURI.contains("/api/auth/register") || requestURI.contains("/api/auth/login")) {
            log.info("Skipping authentication for public endpoint: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = extractJwtFromRequest(request);
            log.info("Extracted JWT: {}", jwt != null ? "present (first 20 chars: " + jwt.substring(0, Math.min(20, jwt.length())) + "...)" : "null");

            if (StringUtils.hasText(jwt) && jwtProvider.validateToken(jwt)) {
                String username = jwtProvider.getUsernameFromToken(jwt);
                log.info("Valid token for user: {}", username);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                log.info("User details authorities: {}", userDetails.getAuthorities());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Authentication set for user: {}", username);
            } else {
                log.warn("No valid JWT token found for request: {}", requestURI);
            }
        } catch (Exception e) {
            log.error("Could not set user authentication: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}