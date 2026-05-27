package com.kennel.mart.kennelmart.controller;

import com.kennel.mart.kennelmart.dto.SellerAnalyticsResponse;
import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.repository.UserRepository;
import com.kennel.mart.kennelmart.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final UserRepository userRepository;

    public AnalyticsController(AnalyticsService analyticsService, UserRepository userRepository) {
        this.analyticsService = analyticsService;
        this.userRepository = userRepository;
    }

    @GetMapping("/seller")
    public ResponseEntity<SellerAnalyticsResponse> getSellerAnalytics(Authentication authentication) {
        String email = authentication.getName();
        User seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        SellerAnalyticsResponse response = analyticsService.getSellerAnalytics(seller.getId());
        return ResponseEntity.ok(response);
    }
}