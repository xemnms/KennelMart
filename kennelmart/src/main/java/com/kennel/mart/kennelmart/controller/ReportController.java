package com.kennel.mart.kennelmart.controller;

import com.kennel.mart.kennelmart.dto.ReportRequest;
import com.kennel.mart.kennelmart.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<Void> submitReport(@Valid @RequestBody ReportRequest request,
                                             Authentication authentication) {
        String reporterEmail = authentication.getName();
        reportService.submitReport(reporterEmail, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}