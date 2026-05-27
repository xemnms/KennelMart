package com.kennel.mart.kennelmart.service;

import com.kennel.mart.kennelmart.dto.SellerAnalyticsResponse;
import java.util.UUID;

public interface AnalyticsService {
    SellerAnalyticsResponse getSellerAnalytics(UUID sellerId);
}