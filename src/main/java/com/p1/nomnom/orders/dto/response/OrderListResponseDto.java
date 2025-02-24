package com.p1.nomnom.orders.dto.response;

import com.p1.nomnom.orders.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class OrderListResponseDto {
    private long totalCount;
    private int page;
    private int size;
    private List<OrderSummary> orders;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderSummary {
        private UUID id;
        private String storeName;
        private Long totalPrice;
        private String status;
        private LocalDateTime createdAt;

        public static OrderSummary from(Order order) {
            return new OrderSummary(
                    order.getId(),
                    order.getStoreName(),
                    order.getTotalPrice(),
                    order.getStatus().name(),
                    order.getCreatedAt()
            );
        }
    }
    public static OrderListResponseDto from(Page<Order> ordersPage) {
        List<OrderSummary> orderSummaries = ordersPage.getContent().stream()
                .map(OrderSummary::from)
                .collect(Collectors.toList());

        return new OrderListResponseDto(
                ordersPage.getTotalElements(),
                ordersPage.getNumber(),
                ordersPage.getSize(),
                orderSummaries
        );
    }
}
