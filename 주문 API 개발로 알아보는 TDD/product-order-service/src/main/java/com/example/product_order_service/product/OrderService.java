package com.example.product_order_service.product;

import org.springframework.stereotype.Component;

@Component
public class OrderService {
    private final OrderPort orderPort;

    public OrderService(final OrderPort orderPort) {
        this.orderPort = orderPort;
    }

    public void createOrder(final CreateOrderRequest request) {
        final Product product = orderPort.getProductById(request.productId());

        final Order order = new Order(product, request.quantity());

        orderPort.save(order);
    }
}