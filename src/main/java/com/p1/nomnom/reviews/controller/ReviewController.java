package com.p1.nomnom.reviews.controller;

import com.p1.nomnom.common.aop.CurrentUser;
import com.p1.nomnom.common.aop.CurrentUserInject;
import com.p1.nomnom.common.aop.UserContext;
import com.p1.nomnom.reviews.dto.request.ReviewRequestDto;
import com.p1.nomnom.reviews.dto.response.ReviewResponseDto;
import com.p1.nomnom.reviews.service.ReviewService;
import com.p1.nomnom.security.aop.RoleCheck;
import com.p1.nomnom.user.entity.User;
import com.p1.nomnom.user.entity.UserRoleEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/reviews")
@RequiredArgsConstructor
@Tag(name = "리뷰 API", description = "리뷰 관련 API")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    @CurrentUserInject
    @Operation(summary = "리뷰 생성", description = "새로운 리뷰를 작성합니다.")
    @ApiResponse(responseCode = "201", description = "리뷰 생성 성공")
    @RoleCheck({UserRoleEnum.CUSTOMER})
    public ReviewResponseDto createReview(
            @RequestBody @Valid ReviewRequestDto reviewRequestDto,
            @CurrentUser @Parameter(hidden = true) UserContext userContext
    ) {
        return reviewService.createReview(reviewRequestDto, userContext);
    }

    @PutMapping("/{reviewId}")
    @CurrentUserInject
    @Operation(summary = "리뷰 수정", description = "리뷰 내용을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 수정 성공")
    @RoleCheck({UserRoleEnum.CUSTOMER})
    public ReviewResponseDto updateReview(
            @Parameter(description = "리뷰 ID", required = true) @PathVariable UUID reviewId,
            @RequestBody @Valid ReviewRequestDto reviewRequestDto,
            @CurrentUser @Parameter(hidden = true) UserContext userContext
    ) {
        return reviewService.updateReview(reviewId, reviewRequestDto, userContext);
    }

    @GetMapping
    @CurrentUserInject
    @Operation(summary = "리뷰 리스트 조회", description = "특정 스토어의 리뷰 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 리스트 조회 성공")
    @RoleCheck({UserRoleEnum.CUSTOMER, UserRoleEnum.OWNER, UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    public Page<ReviewResponseDto> getReviews(
            @Parameter(description = "스토어 ID", required = true) @RequestParam UUID storeId,
            Pageable pageable,
            @CurrentUser @Parameter(hidden = true) UserContext userContext
    ) {
        return reviewService.getReviews(storeId, pageable, userContext);
    }

    @GetMapping("/{reviewId}")
    @CurrentUserInject
    @Operation(summary = "특정 리뷰 조회", description = "리뷰 ID로 특정 리뷰를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "리뷰 조회 성공")
    @RoleCheck({UserRoleEnum.CUSTOMER, UserRoleEnum.OWNER, UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    public ReviewResponseDto getReview(
            @Parameter(description = "스토어 ID", required = true) @PathVariable UUID reviewId,
            @CurrentUser @Parameter(hidden = true) UserContext userContext
    ) {
        return reviewService.getReview(reviewId, userContext);
    }

    @PatchMapping("/{reviewId}")
    @CurrentUserInject
    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제(숨김 처리)합니다.")
    @ApiResponse(responseCode = "204", description = "리뷰 삭제 성공")
    @RoleCheck({UserRoleEnum.CUSTOMER, UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "리뷰 ID", required = true) @PathVariable UUID reviewId,
            @CurrentUser @Parameter(hidden = true) UserContext userContext
    ) {
        reviewService.deleteReview(reviewId, userContext);
        return ResponseEntity.ok().build();
    }
}