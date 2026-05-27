package com.kennel.mart.kennelmart.controller;

import com.kennel.mart.kennelmart.dto.UpdateProfileRequest;
import com.kennel.mart.kennelmart.dto.ChangePasswordRequest;
import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
}