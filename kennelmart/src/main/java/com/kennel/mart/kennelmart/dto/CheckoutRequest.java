package com.kennel.mart.kennelmart.dto;

import com.kennel.mart.kennelmart.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public class CheckoutRequest {

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    public CheckoutRequest() {}

    public CheckoutRequest(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
}