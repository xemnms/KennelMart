package com.kennel.mart.kennelmart.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.enums.VerificationStatus;

/**
 * Repository for User entity persistence operations.
 * 
 * Handles all database operations for User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find a user by email address.
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if user exists by email.
     */
    boolean existsByEmail(String email);

    /**
     * Find all users with a specific verification status.
     */
    List<User> findByVerificationStatus(VerificationStatus status);
}