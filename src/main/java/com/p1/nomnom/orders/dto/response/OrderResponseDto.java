package com.p1.nomnom.orders.dto.response;

import com.p1.nomnom.orderItems.dto.response.OrderItemResponseDto;
import com.p1.nomnom.orders.entity.Order;
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
    private String userName;
    private String storeName;
    private String address;
    private String phone;
    private int totalPrice;
    private String status;
    private String request;
    private LocalDateTime createdAt;
    private List<OrderItemResponseDto> orderItems;
//    enum값 추가 필요
//    private PaymentMethod paymentMethod;

    public static OrderResponseDto from(Order order) {
        return new OrderResponseDto(
                order.getId(),
                "", // TODO: order에서 user 정보 가져오기
                "", // TODO: order에서 store 정보 가져오기
                "", // TODO: order에서 address 정보 가져오기
                order.getPhone(),
                order.getTotalPrice(),
                order.getStatus().name(),
                order.getRequest(),
                order.getCreatedAt(),
                order.getOrderItems().stream()
                        .map(OrderItemResponseDto::from)
                        .collect(Collectors.toList())
        );
    }

}
