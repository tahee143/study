package com.example.product_order_service.order;

import com.example.product_order_service.order.domain.Order;
import com.example.product_order_service.product.domain.DiscountPolicy;
import com.example.product_order_service.product.domain.Product;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class OrderTest {

    @Test
    void getTotalPrice() {
        final Order order = new Order(new Product("상품명", 1000, DiscountPolicy.NONE), 2);
        final int totalPrice = order.getTotalPrice();

        assertThat(totalPrice).isEqualTo(2000);
    }
}