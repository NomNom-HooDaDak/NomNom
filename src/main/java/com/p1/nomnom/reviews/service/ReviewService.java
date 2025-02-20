package com.p1.nomnom.reviews.service;

import com.p1.nomnom.security.aop.UserContext;
import com.p1.nomnom.orders.service.OrderService;
import com.p1.nomnom.reviewImages.entity.ReviewImage;
import com.p1.nomnom.reviewImages.service.ReviewImageService;
import com.p1.nomnom.reviews.dto.request.ReviewRequestDto;
import com.p1.nomnom.reviews.dto.response.ReviewResponseDto;
import com.p1.nomnom.reviews.entity.Review;
import com.p1.nomnom.reviews.repository.ReviewRepository;
import com.p1.nomnom.user.entity.UserRoleEnum;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewImageService reviewImageService;
    private final OrderService orderService;

    // 리뷰 생성
    @Transactional
    public ReviewResponseDto createReview(ReviewRequestDto reviewRequestDto, UserContext userContext) {
        if (userContext.getRole() != UserRoleEnum.CUSTOMER) {
            throw new IllegalArgumentException("Customer만 리뷰를 작성할 수 있습니다.");
        }

        if (!orderService.isOrderDelivered(reviewRequestDto.getOrderId())) {
            throw new IllegalArgumentException("리뷰를 작성하려면 주문이 배달 완료 상태여야 합니다.");
        }

        List<ReviewImage> reviewImages = reviewImageService.createReviewImages(reviewRequestDto.getImages());

        Review review = Review.create(
                userContext.getUserId(),
                userContext.getUsername(),
                reviewRequestDto.getOrderId(),
                reviewRequestDto.getStoreId(),
                reviewRequestDto.getScore(),
                reviewRequestDto.getContent(),
                new ArrayList<>()
        );

        if (reviewImages != null && !reviewImages.isEmpty()) {
            reviewImages.forEach(review::addReviewImage);
        }

        reviewRepository.save(review);
        return ReviewResponseDto.from(review);
    }

    // 리뷰 리스트 조회
    @Transactional(readOnly = true)
    public Page<ReviewResponseDto> getReviews(UUID storeId, Pageable pageable, UserContext userContext) {
        Page<Review> reviews = reviewRepository.findByStoreIdAndHiddenFalse(storeId, pageable);

        return reviews.map(ReviewResponseDto::from);
    }

    @Transactional(readOnly = true)
    public ReviewResponseDto getReview(UUID reviewId, UserContext userContext) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("해당 리뷰는 존재하지 않습니다."));

        return ReviewResponseDto.from(review);
    }

    // 리뷰 수정
    @Transactional
    public ReviewResponseDto updateReview(UUID reviewId, ReviewRequestDto reviewRequestDto, UserContext userContext) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("해당 리뷰는 존재하지 않습니다."));

        if (userContext.getRole() == UserRoleEnum.CUSTOMER && !review.getUserId().equals(userContext.getUserId())) {
            throw new IllegalArgumentException("본인이 작성한 리뷰만 수정할 수 있습니다.");
        }

        if (userContext.getRole() == UserRoleEnum.CUSTOMER) {
            review.update(reviewRequestDto.getScore(), reviewRequestDto.getContent());
            reviewRepository.save(review);
            return ReviewResponseDto.from(review);
        }

        throw new IllegalArgumentException("리뷰 수정 권한이 없습니다.");
    }

    // 리뷰 삭제 (숨김 처리)
    @Transactional
    public void deleteReview(UUID reviewId, UserContext userContext) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰가 존재하지 않습니다."));

        if (userContext.getRole() == UserRoleEnum.CUSTOMER && !review.getUserId().equals(userContext.getUserId())) {
            throw new IllegalArgumentException("본인이 작성한 리뷰만 삭제가 가능합니다");
        }

        if (userContext.getRole() == UserRoleEnum.MANAGER || userContext.getRole() == UserRoleEnum.MASTER) {
            review.cancel(userContext.getUsername());
            reviewRepository.save(review);
            return;
        }

        if (userContext.getRole() == UserRoleEnum.OWNER) {
            throw new IllegalArgumentException("작성자와 관리자 이외는 리뷰를 삭제할 수 없습니다.");
        }
    }
}
