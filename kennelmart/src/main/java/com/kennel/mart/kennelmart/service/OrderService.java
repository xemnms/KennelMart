package com.kennel.mart.kennelmart.service;

import com.kennel.mart.kennelmart.dto.CheckoutRequest;
import com.kennel.mart.kennelmart.dto.OrderResponse;
import com.kennel.mart.kennelmart.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {
    OrderResponse checkout(String userEmail, CheckoutRequest request);
    OrderResponse getOrderById(UUID orderId);
    Page<OrderResponse> getMyOrdersAsBuyer(String userEmail, Pageable pageable);
    Page<OrderResponse> getMyOrdersAsSeller(String userEmail, Pageable pageable);
    OrderResponse updateOrderStatus(UUID orderId, String sellerEmail, OrderStatus newStatus);
}