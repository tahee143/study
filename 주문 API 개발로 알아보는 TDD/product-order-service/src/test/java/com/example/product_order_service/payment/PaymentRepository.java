package com.example.product_order_service.payment;

import java.util.HashMap;
import java.util.Map;

class PaymentRepository {
    private Map<Long, Payment> persistance = new HashMap<>();
    private Long sequence = 0L;


    public void save(final Payment payment) {
        payment.assignId(++sequence);
        persistance.put(payment.getId(), payment);
    }
}
