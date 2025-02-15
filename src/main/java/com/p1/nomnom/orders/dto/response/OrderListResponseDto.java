package com.p1.nomnom.orders.dto.response;

import com.p1.nomnom.orderItems.dto.response.OrderItemResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class OrderListResponseDto {
    private UUID id;
    private String storeName;
    private int totalPrice;
    private String status;
    private LocalDateTime createdAt;
    private List<OrderItemResponseDto> orderItems;
}
