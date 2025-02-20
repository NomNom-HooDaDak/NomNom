package com.p1.nomnom.payment.entity;

import com.p1.nomnom.orderItems.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PaymentInfoOrderItemResponseDto {
    private String foodName;
    private int quantity;
    private Long price;

    public PaymentInfoOrderItemResponseDto(OrderItem orderItem) {
        this.foodName= orderItem.getFoodName();
        this.quantity= orderItem.getQuantity();
        this.price= orderItem.getPrice();
    }

}
