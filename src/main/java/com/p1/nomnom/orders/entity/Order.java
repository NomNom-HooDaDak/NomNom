package com.p1.nomnom.orders.entity;

import com.p1.nomnom.common.entity.BaseEntity;
import com.p1.nomnom.orderItems.entity.OrderItem;
import com.p1.nomnom.payment.entity.Payment;
import com.p1.nomnom.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "order_id")
    private UUID id;

    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String phone;

    @Column(name = "address_id", nullable = false)
    private UUID addressId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(nullable = false)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String request;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "review_id")
    private UUID reviewId;

    public static Order create( UUID storeId, User user, String phone, UUID addressId, String request) {
        return new Order(
                null,
                storeId,
                user,
                phone,
                addressId,
                null,
                request,
                null,
                null,
                Status.PENDING,
                null
        );
    }

    public void updateOrderItemsAndTotalPrice(List<OrderItem> orderItems, Long totalPrice) {
        this.orderItems = orderItems;
        this.totalPrice = totalPrice;
    }
}

