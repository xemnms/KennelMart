package com.kennel.mart.kennelmart.dto;

import com.kennel.mart.kennelmart.enums.ReportStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public class ReportResponse {
    private UUID id;
    private UUID reporterId;
    private String reporterEmail;
    private String targetType;
    private UUID targetId;
    private String reason;
    private ReportStatus status;
    private LocalDateTime createdAt;

    // Builder
    public static Builder builder() { return new Builder(); }
    public static class Builder {
        private ReportResponse response = new ReportResponse();
        public Builder id(UUID id) { response.id = id; return this; }
        public Builder reporterId(UUID reporterId) { response.reporterId = reporterId; return this; }
        public Builder reporterEmail(String reporterEmail) { response.reporterEmail = reporterEmail; return this; }
        public Builder targetType(String targetType) { response.targetType = targetType; return this; }
        public Builder targetId(UUID targetId) { response.targetId = targetId; return this; }
        public Builder reason(String reason) { response.reason = reason; return this; }
        public Builder status(ReportStatus status) { response.status = status; return this; }
        public Builder createdAt(LocalDateTime createdAt) { response.createdAt = createdAt; return this; }
        public ReportResponse build() { return response; }
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getReporterId() { return reporterId; }
    public String getReporterEmail() { return reporterEmail; }
    public String getTargetType() { return targetType; }
    public UUID getTargetId() { return targetId; }
    public String getReason() { return reason; }
    public ReportStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}