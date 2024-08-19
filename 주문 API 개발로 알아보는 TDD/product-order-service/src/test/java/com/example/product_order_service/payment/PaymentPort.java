package com.example.product_order_service.payment;

import com.example.product_order_service.product.Order;

interface PaymentPort {
    Order getOrder(Long orderId);

    void pay(int totalPrice, String cardNumber);

    void save(Payment payment);

}
