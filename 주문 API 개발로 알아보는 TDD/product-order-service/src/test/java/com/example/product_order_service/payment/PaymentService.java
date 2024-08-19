package com.example.product_order_service.payment;

import com.example.product_order_service.product.Order;

class PaymentService {
    private final PaymentPort paymentPort;

    PaymentService(final PaymentPort paymentPort) {
        this.paymentPort = paymentPort;
    }

    public void payment(final PaymentRequest request) {

        final Order order = paymentPort.getOrder(request.orderId());

        final Payment payment = new Payment(order, request.cardNumber());

        paymentPort.pay(payment.getPrice(), payment.getCardNumber());
        paymentPort.save(payment);
    }
}
