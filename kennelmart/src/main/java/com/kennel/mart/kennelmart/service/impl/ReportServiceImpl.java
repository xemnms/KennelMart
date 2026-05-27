package com.kennel.mart.kennelmart.service.impl;

import com.kennel.mart.kennelmart.dto.ReportRequest;
import com.kennel.mart.kennelmart.dto.ReportResponse;
import com.kennel.mart.kennelmart.entity.Report;
import com.kennel.mart.kennelmart.entity.User;
import com.kennel.mart.kennelmart.entity.ProductListing;
import com.kennel.mart.kennelmart.enums.AccountStatus;
import com.kennel.mart.kennelmart.enums.ProductStatus;
import com.kennel.mart.kennelmart.enums.ReportStatus;
import com.kennel.mart.kennelmart.repository.ReportRepository;
import com.kennel.mart.kennelmart.repository.UserRepository;
import com.kennel.mart.kennelmart.repository.ProductListingRepository;
import com.kennel.mart.kennelmart.service.ReportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ProductListingRepository productListingRepository;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ReportServiceImpl.class);

    public ReportServiceImpl(ReportRepository reportRepository, 
                             UserRepository userRepository,
                             ProductListingRepository productListingRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.productListingRepository = productListingRepository;
    }

    @Override
    public void submitReport(String reporterEmail, ReportRequest request) {
        User reporter = userRepository.findByEmail(reporterEmail)
                .orElseThrow(() -> new IllegalArgumentException("Reporter not found"));

        Report report = new Report(reporter, request.getTargetType(), request.getTargetId(), request.getReason());
        reportRepository.save(report);
        log.info("New report submitted by {} for {} {}", reporterEmail, request.getTargetType(), request.getTargetId());
    }

    @Override
    public Page<ReportResponse> getReports(ReportStatus status, Pageable pageable) {
        Page<Report> reports;
        if (status != null) {
            reports = reportRepository.findByStatus(status, pageable);
        } else {
            reports = reportRepository.findAll(pageable);
        }
        return reports.map(this::convertToResponse);
    }

    @Override
    public void resolveReport(UUID reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
        report.setStatus(ReportStatus.RESOLVED);
        reportRepository.save(report);
        log.info("Report {} resolved", reportId);
    }

    @Override
    public void dismissReport(UUID reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
        report.setStatus(ReportStatus.DISMISSED);
        reportRepository.save(report);
        log.info("Report {} dismissed", reportId);
    }

    @Override
    public void markAsReviewing(UUID reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
        report.setStatus(ReportStatus.REVIEWING);
        reportRepository.save(report);
        log.info("Report {} marked as REVIEWING", reportId);
    }

    @Override
    public void suspendReportedUser(UUID userId, UUID reportId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setAccountStatus(AccountStatus.SUSPENDED);
        userRepository.save(user);

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
        report.setStatus(ReportStatus.RESOLVED);
        reportRepository.save(report);
        log.info("User {} suspended and report {} resolved", userId, reportId);
    }

    @Override
    public void deleteReportedListing(UUID listingId, UUID reportId) {
        ProductListing listing = productListingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));
        listing.setStatus(ProductStatus.DELETED);
        productListingRepository.save(listing);

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found"));
        report.setStatus(ReportStatus.RESOLVED);
        reportRepository.save(report);
        log.info("Listing {} soft-deleted and report {} resolved", listingId, reportId);
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