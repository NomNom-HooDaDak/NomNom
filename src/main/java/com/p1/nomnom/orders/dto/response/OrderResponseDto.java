package com.p1.nomnom.orders.dto.response;

import com.p1.nomnom.orders.entity.Order;
import com.p1.nomnom.orderItems.entity.OrderItem;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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
    private String storeName;
    private String userName;
    private UUID addressId;
    private String phone;
    private Long totalPrice;
    private String status;
    private String request;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    protected LocalDateTime deletedAt;
    protected String deletedBy;
    private List<OrderItemDto> orderItems;

    public static OrderResponseDto from(Order order) {
        return new OrderResponseDto(
                order.getId(),
                order.getStoreId(),
                order.getStoreName(),
                order.getUser().getUsername(),
                order.getAddressId(),
                order.getPhone(),
                order.getTotalPrice(),
                order.getStatus().name(),
                order.getRequest(),
                order.getCreatedAt(),
                order.getCreatedBy(),
                order.getUpdatedAt(),
                order.getUpdatedBy(),
                order.getDeletedAt(),
                order.getDeletedBy(),
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
        private Long price;
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