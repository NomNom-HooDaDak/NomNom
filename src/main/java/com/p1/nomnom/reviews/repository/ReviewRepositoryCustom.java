package com.p1.nomnom.reviews.repository;

import com.p1.nomnom.reviews.dto.request.ReviewSearchRequestDto;
import com.p1.nomnom.reviews.entity.Review;
import org.springframework.data.domain.Page;

public interface ReviewRepositoryCustom {
    Page<Review> searchReviews(ReviewSearchRequestDto searchRequest);
}
