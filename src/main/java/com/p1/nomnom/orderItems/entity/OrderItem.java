package com.p1.nomnom.orderItems.entity;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.p1.nomnom.common.entity.BaseEntity;
import com.p1.nomnom.orders.entity.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
@Entity
@Table(name = "p_order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OrderItem extends BaseEntity {
    @Id
    @Column(name = "order_item_id")
    private String id = NanoIdUtils.randomNanoId();

    /*
    Food는 여러 개의 OrderItem에서 참조할 수 있지만, OrderItem은 반드시 하나의 Order에 속해야 함.
    order의 일부이기 때문에 엔티티 직접 연결
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull
    private Order order;

    /*
    OrderItem의 역할은 "주문된 상품 정보만 저장하는 것"
    Food 엔티티를 가져오면 OrderItem이 Food에 종속됨 → Food 삭제 시 OrderItem도 영향을 받을 수 있음.
     */
    @Column(name = "food_id", nullable = false)
    @NotNull
    private String foodId;

    @Column(nullable = false)
    @NotNull
    private int quantity;

    @Column(nullable = false)
    @NotNull
    private int price;
}
