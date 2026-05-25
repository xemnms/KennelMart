package com.kennel.mart.kennelmart.controller;

import com.kennel.mart.kennelmart.dto.ReportRequest;
import com.kennel.mart.kennelmart.entity.Report;
import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.repository.ReportRepository;
import com.kennel.mart.kennelmart.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public ReportController(ReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<Void> submitReport(@Valid @RequestBody ReportRequest request,
                                             Authentication authentication) {
        User reporter = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Report report = new Report(reporter, request.getTargetType(), request.getTargetId(), request.getReason());
        reportRepository.save(report);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}