package com.kennel.mart.kennelmart.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.repository.UserRepository;

/**
 * User Details Service Configuration.
 * 
 * Loads user details from database for authentication.
 * Used by Spring Security during authentication process.
 */
@Configuration
// Lombok removed
public class UserDetailsConfig {

    private UserRepository userRepository;

        public UserDetailsConfig(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

    /**
     * Load user by email (username in this context).
     * 
     * Called by AuthenticationManager during login process.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

            return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                "", // No password
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
            );
        };
    }
}
