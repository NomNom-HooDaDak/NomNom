package com.p1.nomnom.reviews.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ReviewRequestDto {
    @NotNull
    private UUID orderId;

    @NotNull
    private UUID storeId;

    @NotNull
    private int score;

    @NotNull
    private String content;

    private List<String> images;
}
