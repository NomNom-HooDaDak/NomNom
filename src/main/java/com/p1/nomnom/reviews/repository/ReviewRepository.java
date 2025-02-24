package com.p1.nomnom.reviews.repository;

import com.p1.nomnom.reviews.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID>, ReviewRepositoryCustom {
}
