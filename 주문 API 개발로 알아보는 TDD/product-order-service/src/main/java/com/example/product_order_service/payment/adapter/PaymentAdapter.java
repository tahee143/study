package com.example.product_order_service.payment.adapter;

import com.example.product_order_service.order.adapter.OrderRepository;
import com.example.product_order_service.order.domain.Order;
import com.example.product_order_service.payment.application.port.PaymentPort;

import com.example.product_order_service.payment.domain.Payment;
import org.springframework.stereotype.Component;

@Component
class PaymentAdapter implements PaymentPort {
    private final PaymentGateway paymentGateway;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    PaymentAdapter(final PaymentGateway paymentGateway, final PaymentRepository paymentRepository, final OrderRepository orderRepository) {
        this.paymentGateway = paymentGateway;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다."));
    }

    @Override
    public void pay(final int totalPrice, final String cardNumber) {
        paymentGateway.excute(totalPrice, cardNumber);
    }

    @Override
    public void save(final Payment payment) {
        paymentRepository.save(payment);
    }
}
