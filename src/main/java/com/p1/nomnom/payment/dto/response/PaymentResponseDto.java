package com.p1.nomnom.payment.dto.response;

import com.p1.nomnom.orders.entity.Order;
import com.p1.nomnom.payment.entity.Payment;
import com.p1.nomnom.store.entity.Store;
import lombok.*;

import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    // 결제 수단
    private Payment.Method paymentMethod;

    // Card 에 해당하는 결제 승인 상태
    private Payment.CurrentStatus currentStatus;

    private Long userId;
    // private Order order;
    // private Store store;

    public PaymentResponseDto(Payment payment) {
        this.paymentMethod = payment.getMethod();
        this.currentStatus = payment.getCurrentStatus();
        this.userId = payment.getUserId();
        // this.order = payment.getOrder();
        // this.store = payment.getStore();
    }
}
