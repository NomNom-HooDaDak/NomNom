package com.p1.nomnom.reviews.entity;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.p1.nomnom.common.entity.BaseEntity;
import com.p1.nomnom.orders.entity.Order;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
@Entity
@Table(name = "p_reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Review extends BaseEntity {
    @Id
    @Column(name = "review_id")
    private String id = NanoIdUtils.randomNanoId();

    @Column(name = "user_id")
    @NotNull
    private Long userId;

    @Column(name = "user_name")
    @NotNull
    private String userName;

    @NotNull
    private int score;

    @NotNull
    @Column(columnDefinition = "TEXT")
    private String content;

    private String image;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull
    private Order order;
}