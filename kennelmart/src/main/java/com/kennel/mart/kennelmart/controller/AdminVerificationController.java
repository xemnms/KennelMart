package com.kennel.mart.kennelmart.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.enums.VerificationStatus;
import com.kennel.mart.kennelmart.repository.UserRepository;

/**
 * Admin controller for managing identity verification requests.
 * 
 * All endpoints require ADMIN role.
 */
@RestController
@RequestMapping("/api/admin/verification")
@PreAuthorize("hasRole('ADMIN')")
public class AdminVerificationController {

    private final UserRepository userRepository;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AdminVerificationController.class);

    public AdminVerificationController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get all users with PENDING verification status.
     * 
     * @return list of pending users
     */
    @GetMapping("/pending")
    public ResponseEntity<List<User>> getPendingVerifications() {
        List<User> pendingUsers = userRepository.findByVerificationStatus(VerificationStatus.PENDING);
        log.info("Retrieved {} pending verification requests", pendingUsers.size());
        return ResponseEntity.ok(pendingUsers);
    }

    /**
     * Approve a user's verification request.
     * 
     * @param userId UUID of the user to approve
     * @return success message
     */
    @PutMapping("/{userId}/approve")
    public ResponseEntity<?> approveVerification(@PathVariable UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        user.setVerificationStatus(VerificationStatus.VERIFIED);
        userRepository.save(user);

        log.info("User {} (email: {}) has been VERIFIED", userId, user.getEmail());
        return ResponseEntity.ok("User verification approved.");
    }

    /**
     * Reject a user's verification request.
     * 
     * @param userId UUID of the user to reject
     * @return success message
     */
    @PutMapping("/{userId}/reject")
    public ResponseEntity<?> rejectVerification(@PathVariable UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        user.setVerificationStatus(VerificationStatus.REJECTED);
        userRepository.save(user);

        log.info("User {} (email: {}) has been REJECTED", userId, user.getEmail());
        return ResponseEntity.ok("User verification rejected.");
    }
}   