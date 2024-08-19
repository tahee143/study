package com.example.product_order_service.payment;

public class PaymentSteps {
    public static PaymentRequest 주문결제요청_생성() {
        final String cardNumber = "1234-1234-1234-1234";
        final Long orderId = 1L;
        return new PaymentRequest(orderId, cardNumber);
    }
}