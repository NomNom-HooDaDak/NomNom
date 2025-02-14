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
    @NotNull
    private Status status;

    @Column(columnDefinition = "TEXT")
    private String request;

    // 생명주기 생성과 삭제에 밀접한관련이 있나
    // 오더아이템 주문한 목록 / 오더는 주문
    // 오더아이템이 삭제될 경우 주문도 영향이간다
    // 주문 삭제될 경우 오더아이템은 없어야한다 pk fk 참조하는관계 즉 조인가능하다
    // 유저같은경우
    // 주문이 삭제된다고해서 유저가 삭제가 되나요?

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user; -> N+1쿼리 문제

    @Column(name = "user_id")
    @NotNull
    private Long userId;

    @Column(name = "user_name")
    @NotNull
    private String userName;

    @NotNull
    private String phone;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "address_id")
//    private Address address;

    @Column(name = "address_id")
    @NotNull
    private String addressId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotNull
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(name = "total_price")
    @NotNull
    private int totalPrice;

    @NotNull
    private String method;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Review review;
}

