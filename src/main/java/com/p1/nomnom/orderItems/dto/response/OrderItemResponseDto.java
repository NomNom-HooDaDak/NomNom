package com.p1.nomnom.orderItems.dto.response;

import com.p1.nomnom.orderItems.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class OrderItemResponseDto {

    private UUID id;
    private UUID foodId;
    private String foodName;
    private int quantity;
    private int price;

    public static OrderItemResponseDto from(OrderItem orderItem) {
        return new OrderItemResponseDto(
                orderItem.getId(),
                orderItem.getFoodId(),
                "", // TODO: foodId를 기반으로 foodName 조회 필요
                orderItem.getQuantity(),
                orderItem.getPrice()
        );
    }
}
