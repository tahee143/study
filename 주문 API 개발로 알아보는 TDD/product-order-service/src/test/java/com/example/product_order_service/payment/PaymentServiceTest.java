package com.example.product_order_service.payment;

import com.example.product_order_service.product.Order;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.security.spec.ECParameterSpec;

class PaymentServiceTest {

    private PaymentService paymentService;

    @Test
    void 상품주문() {
        final String cardNumber = "1234-1234-1234-1234";
        final Long orderId = 1L;
        final PaymentRequest request = new PaymentRequest(orderId, cardNumber);
        paymentService.payment(request);

    }

    private record PaymentRequest(Long orderId, String cardNumber) {
        private PaymentRequest {
            Assert.notNull(orderId, "주문 ID는 필수입니다.");
            Assert.hasText(cardNumber, "카드 번호는 필수입니다.");
        }
    }

    private class PaymentService {
        private PaymentPort paymentPort;

        public void payment(final PaymentRequest request) {

            Order order = paymentPort.getOrder(request.orderId());

            final Payment payment = new Payment(order, request.cardNumber());
        }
    }

    private interface PaymentPort {
        Order getOrder(Long orderId);
    }

    private class Payment {
        private final Order order;
        private final String cardNumber;

        public Payment(final Order order, final String cardNumber) {
            Assert.notNull(order, "주문은 필수입니다.");
            Assert.hasText(cardNumber, "카드 번호는 필수입니다.");
            this.order = order;
            this.cardNumber = cardNumber;
        }
    }
}
