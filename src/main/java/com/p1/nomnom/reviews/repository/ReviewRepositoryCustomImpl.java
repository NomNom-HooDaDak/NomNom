package com.p1.nomnom.reviews.repository;

import com.p1.nomnom.reviews.dto.request.ReviewSearchRequestDto;
import com.p1.nomnom.reviews.entity.QReview;
import com.p1.nomnom.reviewImages.entity.QReviewImage;
import com.p1.nomnom.reviews.entity.Review;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Review> searchReviews(ReviewSearchRequestDto searchRequest) {
        QReview review = QReview.review;
        QReviewImage reviewImage = QReviewImage.reviewImage;

        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(review.storeId.eq(searchRequest.getStoreId()));
        whereClause.and(review.hidden.isFalse());

        // 이미지가 있는 리뷰만 필터링
        if (searchRequest.isHasImage()) {
            whereClause.and(review.reviewImages.any().isNotNull());
        }

        // 정렬 조건
        OrderSpecifier<?> orderSpecifier = switch (Optional.ofNullable(searchRequest.getSort()).orElse("recent")) {
            case "별점 높은순" -> review.score.desc();
            case "별점 낮은순" -> review.score.asc();
            case "과거순" -> review.createdAt.asc();
            default -> review.createdAt.desc();
        };

        // 쿼리 실행
        var query = queryFactory
                .selectFrom(review)
                .where(whereClause)
                .orderBy(orderSpecifier)
                .offset(searchRequest.toPageable().getOffset())
                .limit(searchRequest.toPageable().getPageSize());

        List<Review> reviews = query.fetch();

        return PageableExecutionUtils.getPage(reviews, searchRequest.toPageable(), query::fetchCount);
    }
}
