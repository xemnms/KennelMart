package com.kennel.mart.kennelmart.controller;

import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.enums.AccountStatus;
import com.kennel.mart.kennelmart.enums.VerificationStatus;
import com.kennel.mart.kennelmart.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserRepository userRepository;

    public AdminUserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<Page<User>> getUsers(
            @RequestParam(required = false) VerificationStatus verificationStatus,
            @RequestParam(required = false) AccountStatus accountStatus,
            Pageable pageable) {
        if (verificationStatus != null && accountStatus != null) {
            return ResponseEntity.ok(userRepository.findByVerificationStatusAndAccountStatus(verificationStatus, accountStatus, pageable));
        } else if (verificationStatus != null) {
            return ResponseEntity.ok(userRepository.findByVerificationStatus(verificationStatus, pageable));
        } else if (accountStatus != null) {
            return ResponseEntity.ok(userRepository.findByAccountStatus(accountStatus, pageable));
        } else {
            return ResponseEntity.ok(userRepository.findAll(pageable));
        }
    }

    @PutMapping("/{userId}/suspend")
    public ResponseEntity<Void> suspendUser(@PathVariable UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setAccountStatus(AccountStatus.SUSPENDED);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setAccountStatus(AccountStatus.ACTIVE);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/verify")
    public ResponseEntity<Void> verifyUser(@PathVariable UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setVerificationStatus(VerificationStatus.VERIFIED);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/reject-verification")
    public ResponseEntity<Void> rejectVerification(@PathVariable UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setVerificationStatus(VerificationStatus.REJECTED);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
}