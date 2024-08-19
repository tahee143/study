package com.example.product_order_service.payment;

public class ConsolePaymentGateway implements PaymentGateway {
    @Override
    public void excute(final int totalPrice, final String cardNumber) {
        System.out.println("결제완료");
    }
}
