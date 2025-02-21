package com.p1.nomnom.payment.dto.response;

import com.p1.nomnom.orderItems.entity.OrderItem;
import com.p1.nomnom.orders.entity.Order;
import com.p1.nomnom.orders.entity.Status;
import com.p1.nomnom.payment.entity.Payment;
import com.p1.nomnom.payment.entity.PaymentInfoOrderItemResponseDto;
import com.p1.nomnom.store.entity.Store;
import lombok.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    // 결제 수단
    private Payment.Method paymentMethod;

    // Card 에 해당하는 결제 승인 상태
    private Payment.Status status;

    private Long userId;

    private UUID orderId;
    private List<PaymentInfoOrderItemResponseDto> orderItemList = new ArrayList<>();

    // private UUID storeId;
    private String storeName;

    // 숨김 처리 여부
    private boolean isDeleted = false;

    public PaymentResponseDto(Payment payment) {
        this.paymentMethod = payment.getMethod();
        this.status = payment.getStatus();
        this.userId = payment.getUserId();
        // Collection의 addAll()은 리스트타입을 받아서 해당 리스트의 객체를 모두 추가해주는 기능
        this.orderId = payment.getOrder().getId();
        this.orderItemList = customOrderItemList(payment.getOrder().getOrderItems());
        // this.storeId = payment.getStore().getId();
        this.storeName = payment.getStore().getName();
        this.isDeleted = payment.isDeleted();
    }

    private List<PaymentInfoOrderItemResponseDto> customOrderItemList(List<OrderItem> orderItems) {
        // 무슨 재귀 호출 문제로 stackoverflow 발생해서 하기 코드 일단 추가
        if (orderItems == null) {
            return new ArrayList<>();
        }

                                // orderItem -> new PaymentInfoOrderItemResponseDto(orderItem)
        return orderItems.stream().map(PaymentInfoOrderItemResponseDto::new)
                .toList(); // .collect(Collectors.toList());
    }
}
