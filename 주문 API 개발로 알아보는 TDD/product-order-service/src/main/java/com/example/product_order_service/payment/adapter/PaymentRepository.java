package com.example.product_order_service.payment.adapter;


import com.example.product_order_service.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

interface PaymentRepository extends JpaRepository<Payment, Long> {
}
