package com.kennel.mart.kennelmart.controller;

import com.kennel.mart.kennelmart.dto.UpdateProfileRequest;
import com.kennel.mart.kennelmart.dto.ChangePasswordRequest;
import com.kennel.mart.kennelmart.dto.UserSearchResponse;
import com.kennel.mart.kennelmart.dto.UserPublicProfileResponse;
import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.enums.AccountStatus;
import com.kennel.mart.kennelmart.service.UserService;
import com.kennel.mart.kennelmart.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateProfile(@Valid @RequestBody UpdateProfileRequest request,
                                              Authentication authentication) {
        String email = authentication.getName();
        User updated = userService.updateProfile(email, request);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                               Authentication authentication) {
        String email = authentication.getName();
        userService.changePassword(email, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<Page<UserSearchResponse>> searchUsers(
            @RequestParam String keyword,
            @PageableDefault(size = 12) Pageable pageable) {
        Page<User> userPage = userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseAndAccountStatus(
                keyword, keyword, AccountStatus.ACTIVE, pageable);
        Page<UserSearchResponse> response = userPage.map(UserSearchResponse::new);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/public")
    public ResponseEntity<UserPublicProfileResponse> getPublicProfile(@PathVariable UUID userId) {
        User user = userRepository.findByIdAndAccountStatus(userId, AccountStatus.ACTIVE)
                .orElseThrow(() -> new RuntimeException("User not found or not active"));
        return ResponseEntity.ok(new UserPublicProfileResponse(user));
    }
}