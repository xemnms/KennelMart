package com.kennel.mart.kennelmart.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.enums.AccountStatus;
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
     * Find all users with a specific verification status (return List).
     */
    List<User> findByVerificationStatus(VerificationStatus status);

    /**
     * Find users by verification status with pagination.
     */
    Page<User> findByVerificationStatus(VerificationStatus status, Pageable pageable);

    /**
     * Find users by account status with pagination.
     */
    Page<User> findByAccountStatus(AccountStatus status, Pageable pageable);

    /**
     * Find users by both verification status and account status with pagination.
     */
    Page<User> findByVerificationStatusAndAccountStatus(VerificationStatus vStatus, AccountStatus aStatus, Pageable pageable);
}