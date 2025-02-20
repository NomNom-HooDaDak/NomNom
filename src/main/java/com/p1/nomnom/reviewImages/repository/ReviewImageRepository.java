package com.p1.nomnom.reviewImages.repository;

import com.p1.nomnom.reviewImages.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, UUID> {
}
