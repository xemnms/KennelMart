package com.kennel.mart.kennelmart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class ReportRequest {
    @NotBlank
    private String targetType; // "LISTING" or "USER"
    @NotNull
    private UUID targetId;
    @NotBlank
    private String reason;

    // getters/setters
    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }
    public UUID getTargetId() { return targetId; }
    public void setTargetId(UUID targetId) { this.targetId = targetId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}