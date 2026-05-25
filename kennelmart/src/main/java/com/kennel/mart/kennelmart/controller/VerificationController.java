package com.kennel.mart.kennelmart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.kennel.mart.kennelmart.dto.VerificationRequest;
import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.enums.VerificationStatus;
import com.kennel.mart.kennelmart.repository.UserRepository;

import jakarta.validation.Valid;

/**
 * Controller for identity verification operations.
 * 
 * Endpoints:
 * - POST /api/verification/submit – submit student/faculty ID for verification
 * - GET /api/verification/status – get current verification status
 */
@RestController
@RequestMapping("/api/verification")
public class VerificationController {

    private final UserRepository userRepository;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(VerificationController.class);

    public VerificationController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Submit student/faculty ID for verification.
     * 
     * @param request contains studentOrFacultyId (and optional image URL)
     * @param auth current authenticated user
     * @return success message
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submitVerification(@Valid @RequestBody VerificationRequest request,
                                                Authentication auth) {
        String email = auth.getName();
        log.info("Verification submission from user: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update user with submitted student ID (if not already set)
        if (user.getStudentOrFacultyId() == null || user.getStudentOrFacultyId().isEmpty()) {
            user.setStudentOrFacultyId(request.getStudentOrFacultyId());
        }

        // Set status to PENDING (admin will review)
        user.setVerificationStatus(VerificationStatus.PENDING);
        userRepository.save(user);

        log.info("Verification status updated to PENDING for user: {}", email);
        return ResponseEntity.ok("Verification request submitted. Awaiting admin approval.");
    }

    /**
     * Get current verification status.
     * 
     * @param auth current authenticated user
     * @return verification status (PENDING, VERIFIED, REJECTED)
     */
    @GetMapping("/status")
    public ResponseEntity<VerificationStatus> getStatus(Authentication auth) {
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user.getVerificationStatus());
    }
}