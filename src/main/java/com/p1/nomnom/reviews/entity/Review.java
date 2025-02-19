package com.p1.nomnom.reviews.entity;

import com.p1.nomnom.common.entity.BaseEntity;
import com.p1.nomnom.orders.entity.Status;
import com.p1.nomnom.reviewImages.entity.ReviewImage;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "p_reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "review_id")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    @Column(nullable = false)
    private int score;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> reviewImages = new ArrayList<>();

    private boolean hidden;

    public void addReviewImage(ReviewImage reviewImage) {
        this.reviewImages.add(reviewImage);
        reviewImage.setReview(this);
    }

    public static Review create(Long userId, String userName, UUID orderId, UUID storeId, int score, String content, List<ReviewImage> imageUrls) {
        return new Review(
                null,
                userId,
                userName,
                orderId,
                storeId,
                score,
                content,
                imageUrls,
                false
        );
    }

    public void update(int score, String content) {
        this.score = score;
        this.content = content;
    }

    public void cancel(String deletedBy) {
        markAsDeleted(deletedBy);
        this.hidden = true;
    }
}