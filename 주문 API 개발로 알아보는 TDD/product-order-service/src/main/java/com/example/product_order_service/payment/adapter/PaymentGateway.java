package com.example.product_order_service.payment.adapter;

interface PaymentGateway {
    void excute(int totalPrice, String cardNumber);
}
