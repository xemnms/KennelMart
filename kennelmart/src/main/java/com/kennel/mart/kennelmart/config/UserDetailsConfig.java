package com.kennel.mart.kennelmart.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.enums.AccountStatus;
import com.kennel.mart.kennelmart.enums.VerificationStatus;
import com.kennel.mart.kennelmart.repository.UserRepository;

/**
 * User Details Service Configuration.
 * 
 * Loads user details from database for authentication.
 * Used by Spring Security during authentication process.
 * 
 * Now includes:
 * - Password retrieval (stored BCrypt hash)
 * - Account status check (ACTIVE / SUSPENDED)
 * - Verification-based authority assignment for feature gating
 */
@Configuration
public class UserDetailsConfig {

    private final UserRepository userRepository;

    public UserDetailsConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Load user by email (username in this context).
     * 
     * Called by AuthenticationManager during login process.
     * 
     * @param email user's email address
     * @return UserDetails object with authorities
     * @throws UsernameNotFoundException if user not found or account not active
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

            // Check if account is active
            if (user.getAccountStatus() != AccountStatus.ACTIVE) {
                throw new UsernameNotFoundException("Account is suspended. Please contact support.");
            }

            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

            // Verified users receive extra authority used by security rules.
            if (user.getVerificationStatus() == VerificationStatus.VERIFIED) {
                authorities.add(new SimpleGrantedAuthority("VERIFIED_USER"));
            }

            return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),  // now returns the stored BCrypt hash
                authorities
            );
        };
    }
}