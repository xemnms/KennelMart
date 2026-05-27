package com.kennel.mart.kennelmart.controller;

import com.kennel.mart.kennelmart.dto.ReportResponse;
import com.kennel.mart.kennelmart.enums.ReportStatus;
import com.kennel.mart.kennelmart.service.ReportService;
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

    private final ReportService reportService;

    public AdminReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public ResponseEntity<Page<ReportResponse>> getReports(
            @RequestParam(required = false) ReportStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(reportService.getReports(status, pageable));
    }

    @PutMapping("/{reportId}/resolve")
    public ResponseEntity<Void> resolveReport(@PathVariable UUID reportId) {
        reportService.resolveReport(reportId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{reportId}/dismiss")
    public ResponseEntity<Void> dismissReport(@PathVariable UUID reportId) {
        reportService.dismissReport(reportId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{reportId}/review")
    public ResponseEntity<Void> markAsReviewing(@PathVariable UUID reportId) {
        reportService.markAsReviewing(reportId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{reportId}/suspend-user/{userId}")
    public ResponseEntity<Void> suspendUserFromReport(@PathVariable UUID reportId, @PathVariable UUID userId) {
        reportService.suspendReportedUser(userId, reportId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{reportId}/delete-listing/{listingId}")
    public ResponseEntity<Void> deleteListingFromReport(@PathVariable UUID reportId, @PathVariable UUID listingId) {
        reportService.deleteReportedListing(listingId, reportId);
        return ResponseEntity.ok().build();
    }
}