package com.p1.nomnom.orders.entity;

import com.p1.nomnom.common.entity.BaseEntity;
import com.p1.nomnom.orderItems.entity.OrderItem;
import com.p1.nomnom.payment.entity.Payment;
import com.p1.nomnom.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.GenericGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Table(name = "p_orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "order_id")
    private UUID id;

    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String phone;

    @Column(name = "address_id", nullable = false)
    private UUID addressId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String request;

    @Column(name = "total_price")
    private Long totalPrice;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(name = "review_id")
    private UUID reviewId;

    private boolean hidden;

    public static Order create( UUID storeId, String storeName, User user, String phone, UUID addressId, String request) {
        return new Order(
                null,
                storeId,
                storeName,
                user,
                phone,
                addressId,
                new ArrayList<>(),
                request,
                null,
                null,
                Status.PENDING,
                null,
                false
        );
    }

    public void updateOrderItemsAndTotalPrice(List<OrderItem> orderItems, Long totalPrice) {
        this.orderItems.addAll(orderItems);
        this.totalPrice = totalPrice;
    }

    public void cancel() {
        this.status = Status.CANCELED;
    }

    public void delete(String deletedBy) {
        this.hidden = true;
        markAsDeleted(deletedBy);
    }
}

