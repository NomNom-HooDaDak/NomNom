package com.p1.nomnom.reviews.repository;

import com.p1.nomnom.reviews.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Page<Review> findByStoreIdAndHiddenFalse(UUID storeId, Pageable pageable);
}
