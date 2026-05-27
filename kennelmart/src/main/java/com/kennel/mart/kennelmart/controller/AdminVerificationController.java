package com.kennel.mart.kennelmart.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.enums.VerificationStatus;
import com.kennel.mart.kennelmart.repository.UserRepository;
import com.kennel.mart.kennelmart.service.NotificationService;

@RestController
@RequestMapping("/api/admin/verification")
@PreAuthorize("hasRole('ADMIN')")
public class AdminVerificationController {

    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AdminVerificationController.class);

    public AdminVerificationController(UserRepository userRepository, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @GetMapping("/pending")
    public ResponseEntity<List<User>> getPendingVerifications() {
        List<User> pendingUsers = userRepository.findByVerificationStatus(VerificationStatus.PENDING);
        log.info("Retrieved {} pending verification requests", pendingUsers.size());
        return ResponseEntity.ok(pendingUsers);
    }

    @PutMapping("/{userId}/approve")
    public ResponseEntity<?> approveVerification(@PathVariable UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        user.setVerificationStatus(VerificationStatus.VERIFIED);
        userRepository.save(user);

        notificationService.sendNotification(
                user.getId(),
                "Identity Verification Approved",
                "Your account has been verified. You can now create listings and place orders.",
                "VERIFICATION",
                null
        );

        log.info("User {} (email: {}) has been VERIFIED", userId, user.getEmail());
        return ResponseEntity.ok("User verification approved.");
    }

    @PutMapping("/{userId}/reject")
    public ResponseEntity<?> rejectVerification(@PathVariable UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        user.setVerificationStatus(VerificationStatus.REJECTED);
        userRepository.save(user);

        notificationService.sendNotification(
                user.getId(),
                "Identity Verification Rejected",
                "Your identity verification was rejected. Please submit a valid student/faculty ID.",
                "VERIFICATION",
                null
        );

        log.info("User {} (email: {}) has been REJECTED", userId, user.getEmail());
        return ResponseEntity.ok("User verification rejected.");
    }
}