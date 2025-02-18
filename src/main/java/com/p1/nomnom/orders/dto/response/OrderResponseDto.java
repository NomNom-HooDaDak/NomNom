package com.p1.nomnom.orders.dto.response;

import com.p1.nomnom.orders.entity.Order;
import com.p1.nomnom.orderItems.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private List<OrderItemDto> orderItems; // ✅ 내부 클래스 사용

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
                order.getOrderItems().stream()
                        .map(OrderItemDto::from) // ✅ 내부 OrderItemDto 사용
                        .collect(Collectors.toList())
        );
    }

    @Getter
    @NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    @AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
    public static class OrderItemDto {
        private String foodName;
        private int price;
        private int quantity;

        public static OrderItemDto from(OrderItem orderItem) {
            return new OrderItemDto(
                    orderItem.getFoodName(),
                    orderItem.getPrice(),
                    orderItem.getQuantity()
            );
        }
    }
}
