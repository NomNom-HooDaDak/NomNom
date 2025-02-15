package com.p1.nomnom.reviewImages.entity;

import com.p1.nomnom.common.entity.BaseEntity;
import com.p1.nomnom.reviews.entity.Review;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "p_reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ReviewImage extends BaseEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "review_image_id")
    private UUID id;

    @Column(nullable = false)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    public void setReview(Review review) {
        this.review = review;
        if (review != null && !review.getReviewImages().contains(this)) {
            review.getReviewImages().add(this);
        }
    }
}