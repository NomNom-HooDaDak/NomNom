package com.p1.nomnom.reviews.dto.response;

import com.p1.nomnom.orders.dto.response.OrderResponseDto;
import com.p1.nomnom.reviews.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ReviewResponseDto {
    private UUID id;
    private UUID orderId;
    private UUID storeId;
    private String userName;
    private int score;
    private String content;
    private List<String> images;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime updatedAt;
    private String updatedBy;
    protected LocalDateTime deletedAt;
    protected String deletedBy;//
    public static ReviewResponseDto from(Review review) {
        return new ReviewResponseDto(
                review.getId(),
                review.getOrderId(),
                review.getStoreId(),
                review.getUserName(),
                review.getScore(),
                review.getContent(),
                review.getReviewImages().stream()
                        .map(image -> image.getUrl())
                        .collect(Collectors.toList()),
                review.getCreatedAt(),
                review.getCreatedBy(),
                review.getUpdatedAt(),
                review.getUpdatedBy(),
                review.getDeletedAt(),
                review.getDeletedBy()
        );
    }
}
