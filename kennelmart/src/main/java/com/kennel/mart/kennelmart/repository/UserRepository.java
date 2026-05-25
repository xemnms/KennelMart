package com.kennel.mart.kennelmart.repository;

import com.kennel.mart.kennelmart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for User entity persistence operations.
 * 
 * Handles all database operations for User entities.
 * 
 * OOP Principles:
 * - Abstraction: Interface-based design
 * - Single Responsibility: Database access only
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Find a user by email address.
     * 
     * @param email the user's email
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if user exists by email.
     * 
     * @param email the email to check
     * @return true if user exists with this email
     */
    boolean existsByEmail(String email);

    /**
     * Find a user by student/faculty ID.
     * 
     * @param studentOrFacultyId the NU student/faculty ID
     * @return Optional containing the user if found
     */
    Optional<User> findByStudentOrFacultyId(String studentOrFacultyId);
}
