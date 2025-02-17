package com.p1.nomnom.orders.dto.response;

import com.p1.nomnom.orderItems.entity.OrderItem;
import com.p1.nomnom.orders.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class OrderResponseDto {
    private UUID id;
    private UUID storeId;
    private String userName;
    private UUID addressId;
    private String phone;
    private Long totalPrice;
    private String status;
    private String request;
    private LocalDateTime createdAt;
    private List<OrderItem> orderItems;

    // 정적팩토리메서드
    public static OrderResponseDto from(Order order) {
        return new OrderResponseDto(
                order.getId(),
                order.getStoreId(),
                order.getUser().getUsername(),
                order.getAddressId(),
                order.getPhone(),
                order.getTotalPrice(),
                order.getStatus().name(),
                order.getRequest(),
                order.getCreatedAt(),
                order.getOrderItems()
        );
    }
}
