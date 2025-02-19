package com.p1.nomnom.reviewImages.service;

import com.p1.nomnom.reviewImages.entity.ReviewImage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewImageService {
    public List<ReviewImage> createReviewImages(List<String> imageUrls) {
        return imageUrls.stream()
                .map(ReviewImage::create)
                .collect(Collectors.toList());
    }
}
