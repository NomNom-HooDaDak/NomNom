package com.p1.nomnom.reviews.dto.response;

import com.p1.nomnom.reviews.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ReviewListResponseDto {

    private long totalCount;
    private int page;
    private int size;
    private List<ReviewSummary> reviews;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReviewSummary {
        private UUID id;
        private UUID orderId;
        private UUID storeId;
        private String userName;
        private int score;
        private String content;
        private List<String> images;

        public static ReviewSummary from(Review review) {
            return new ReviewSummary(
                    review.getId(),
                    review.getOrderId(),
                    review.getStoreId(),
                    review.getUserName(),
                    review.getScore(),
                    review.getContent(),
                    review.getReviewImages().stream()
                            .map(image -> image.getUrl())
                            .collect(Collectors.toList())
            );
        }
    }

    // 페이지 정보를 기반으로 DTO 변환
    public static ReviewListResponseDto from(Page<Review> reviewsPage) {
        List<ReviewSummary> reviewSummaries = reviewsPage.getContent().stream()
                .map(ReviewSummary::from)
                .collect(Collectors.toList());

        return new ReviewListResponseDto(
                reviewsPage.getTotalElements(),
                reviewsPage.getNumber(),
                reviewsPage.getSize(),
                reviewSummaries
        );
    }
}
