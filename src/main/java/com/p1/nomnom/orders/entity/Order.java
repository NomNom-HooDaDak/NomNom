package com.p1.nomnom.orders.entity;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.p1.nomnom.common.entity.BaseEntity;
import com.p1.nomnom.orderItems.entity.OrderItem;
import com.p1.nomnom.reviews.entity.Review;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "p_orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Order extends BaseEntity {
    @Id
    @Column(name = "order_id")
    private String id = NanoIdUtils.randomNanoId();;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(columnDefinition = "TEXT")
    private String request;

//    유저 엔티티 생성되면 해제 예정
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;

//    // 나중을 위한 스냅샷 (아직 미사용)
//    @Column(nullable = false)
//    private String phone;

    @Column(name = "address_id", nullable = false)
    private String addressId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(nullable = false)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @Column(nullable = false)
    private String method;

    @Column(name = "review_id")
    private String reviewId;

    //유저 엔티티 추가되면 해제
//    public void setUser(User user) {
//        this.user = user;
//        if (!user.getOrders().contains(this)) {
//            user.getOrders().add(this);
//        }
//    }
}

