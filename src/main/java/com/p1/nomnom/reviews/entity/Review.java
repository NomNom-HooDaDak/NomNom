package com.p1.nomnom.reviews.entity;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.p1.nomnom.common.entity.BaseEntity;
import com.p1.nomnom.orderItems.entity.OrderItem;
import com.p1.nomnom.orders.entity.Order;
import com.p1.nomnom.reviewImages.entity.ReviewImage;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name", nullable = false)
    @NotNull
    private String userName;

    @Column(nullable = false)
    private int score;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(nullable = false)
    private List<ReviewImage> reviewImages = new ArrayList<>();

    @Column(name = "order_id", nullable = false)
    private String orderId;
}