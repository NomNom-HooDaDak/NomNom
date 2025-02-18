package com.p1.nomnom.orders.dto.request;

import com.p1.nomnom.orderItems.dto.OrderItemDto;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class OrderRequestDto {
    @NotNull
    private UUID storeId;

    @NotNull
    private UUID addressId;

    @NotNull
    private String phone;

    private String request;

//    enum 값 추가 필요
//    @NotNull
//    private PaymentMethod method;

    @NotNull
    private List<OrderItemRequestDto> orderItems;
}