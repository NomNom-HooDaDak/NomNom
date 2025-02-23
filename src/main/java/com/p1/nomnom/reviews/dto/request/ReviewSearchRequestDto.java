package com.p1.nomnom.reviews.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Setter
@Getter
public class ReviewSearchRequestDto {
    private UUID storeId;
    private String sort;
    private boolean hasImage;
    private int page;
    private int size;

    public Pageable toPageable() {
        return PageRequest.of(page, size);
    }
}

