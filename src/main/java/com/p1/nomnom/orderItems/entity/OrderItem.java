package com.p1.nomnom.orderItems.entity;

import com.p1.nomnom.common.entity.BaseEntity;
import com.p1.nomnom.orders.entity.Order;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "p_order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "order_item_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "food_id", nullable = false)
    private UUID foodId;

    @Column(nullable = false)
    private String foodName;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private Long price;

    public static OrderItem create(Order order, UUID foodId, String foodName, int quantity, Long price) {
        return new OrderItem(
                null,
                order,
                foodId,
                foodName,
                quantity,
                price
        );
    }
}
