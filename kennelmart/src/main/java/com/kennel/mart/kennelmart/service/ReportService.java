package com.kennel.mart.kennelmart.service;

import com.kennel.mart.kennelmart.dto.ReportRequest;
import com.kennel.mart.kennelmart.dto.ReportResponse;
import com.kennel.mart.kennelmart.enums.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface ReportService {
    void submitReport(String reporterEmail, ReportRequest request);
    Page<ReportResponse> getReports(ReportStatus status, Pageable pageable);
    void resolveReport(UUID reportId);
    void dismissReport(UUID reportId);
    void markAsReviewing(UUID reportId);
    
    // New methods for acting on reported targets
    void suspendReportedUser(UUID userId, UUID reportId);
    void deleteReportedListing(UUID listingId, UUID reportId);
}