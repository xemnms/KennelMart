package com.kennel.mart.kennelmart.controller;

import com.kennel.mart.kennelmart.dto.ReportResponse;
import com.kennel.mart.kennelmart.entity.Report;
import com.kennel.mart.kennelmart.enums.ReportStatus;
import com.kennel.mart.kennelmart.repository.ReportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/reports")
@PreAuthorize("hasRole('ADMIN')")
public class AdminReportController {

    private final ReportRepository reportRepository;

    public AdminReportController(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @GetMapping
    public ResponseEntity<Page<ReportResponse>> getReports(
            @RequestParam(required = false) ReportStatus status,
            Pageable pageable) {
        Page<Report> reports = (status != null) 
                ? reportRepository.findByStatus(status, pageable)
                : reportRepository.findAll(pageable);
        return ResponseEntity.ok(reports.map(this::convertToResponse));
    }

    @PutMapping("/{reportId}/resolve")
    public ResponseEntity<Void> resolveReport(@PathVariable UUID reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
        report.setStatus(ReportStatus.RESOLVED);
        reportRepository.save(report);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{reportId}/dismiss")
    public ResponseEntity<Void> dismissReport(@PathVariable UUID reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
        report.setStatus(ReportStatus.DISMISSED);
        reportRepository.save(report);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{reportId}/review")
    public ResponseEntity<Void> markAsReviewing(@PathVariable UUID reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
        report.setStatus(ReportStatus.REVIEWING);
        reportRepository.save(report);
        return ResponseEntity.ok().build();
    }

    private ReportResponse convertToResponse(Report report) {
        return ReportResponse.builder()
                .id(report.getId())
                .reporterId(report.getReporter().getId())
                .reporterEmail(report.getReporter().getEmail())
                .targetType(report.getTargetType())
                .targetId(report.getTargetId())
                .reason(report.getReason())
                .status(report.getStatus())
                .createdAt(report.getCreatedAt())
                .build();
    }
}