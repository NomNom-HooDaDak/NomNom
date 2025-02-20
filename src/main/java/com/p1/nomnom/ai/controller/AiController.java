package com.p1.nomnom.ai.controller;

import com.p1.nomnom.ai.dto.request.AiRequestDto;
import com.p1.nomnom.ai.dto.response.AiResponseDto;
import com.p1.nomnom.ai.service.AiService;
import com.p1.nomnom.security.aop.CurrentUser;
import com.p1.nomnom.security.aop.CurrentUserInject;
import com.p1.nomnom.security.aop.RoleCheck;
import com.p1.nomnom.security.aop.UserContext;
import com.p1.nomnom.user.entity.UserRoleEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "Ai 음식 설명 API", description = "AI 음식 설명 API")
public class AiController {

    private final AiService aiService;

    // AI 상품 설명 자동 생성
    @RoleCheck({UserRoleEnum.OWNER, UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @PostMapping("/foods/description")
    @CurrentUserInject
    @Operation(summary = "AI 음식 설명 생성", description = "음식 설명을 생성합니다")
    @ApiResponse(responseCode = "200", description = "음식 설명 생성 성공")
    public AiResponseDto generateFoodDescription(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "AI가 음식 설명을 생성하는데 필요한 요청 데이터"
            ) @RequestBody AiRequestDto requestDto,
            @Parameter(hidden = true) @CurrentUser UserContext userContext
    ) {
        return aiService.getAiAnswer(requestDto, userContext);
    }

    // 모든 AI 응답 조회 (검색 + 페이지네이션 + 정렬)
    @RoleCheck({UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @GetMapping("/all")
    @Operation(summary = "모든 AI 응답 조회", description = "모든 AI 응답을 검색, 페이지네이션, 정렬하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "AI 응답 목록 조회 성공")
    public ResponseEntity<List<AiResponseDto>> getAllAiAnswers(
            @Parameter(description = "페이지 번호 (0부터 시작)")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "한 페이지에 포함될 응답 개수")
            @RequestParam(value = "size", defaultValue = "10") int size,
            @Parameter(description = "정렬 기준 필드 (예: createdAt)")
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @Parameter(description = "정렬 방식 (asc 또는 desc)")
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder) {

        Sort sort = Sort.by("desc".equals(sortOrder) ? Sort.Order.desc(sortBy) : Sort.Order.asc(sortBy));
        return ResponseEntity.ok(aiService.getAllAiAnswers(page, size, sort));
    }

    // 특정 가게의 AI 응답 조회
    @RoleCheck({UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @GetMapping("/store/{storeId}")
    @Operation(summary = "특정 가게의 AI 응답 조회", description = "해당 가게의 AI 응답을 페이지네이션 및 정렬하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "가게별 AI 응답 조회 성공")
    public ResponseEntity<List<AiResponseDto>> getAiAnswersByStore(
            @Parameter(description = "조회할 가게의 UUID")
            @PathVariable UUID storeId,
            @Parameter(description = "페이지 번호 (0부터 시작)")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "한 페이지에 포함될 응답 개수")
            @RequestParam(value = "size", defaultValue = "10") int size,
            @Parameter(description = "정렬 기준 필드 (예: createdAt)")
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @Parameter(description = "정렬 방식 (asc 또는 desc)")
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder) {

        Sort sort = Sort.by("desc".equals(sortOrder) ? Sort.Order.desc(sortBy) : Sort.Order.asc(sortBy));
        return ResponseEntity.ok(aiService.getAiAnswersByStore(storeId, page, size, sort));
    }

    // 특정 키워드로 AI 응답 검색
    @RoleCheck({UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @GetMapping("/search")
    @Operation(summary = "AI 응답 검색", description = "키워드를 기반으로 AI 응답을 검색합니다.")
    @ApiResponse(responseCode = "200", description = "AI 응답 검색 성공")
    public ResponseEntity<List<AiResponseDto>> searchAiAnswersByKeyword(
            @Parameter(description = "검색할 키워드")
            @RequestParam String keyword,
            @Parameter(description = "페이지 번호 (0부터 시작)")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "한 페이지에 포함될 응답 개수")
            @RequestParam(value = "size", defaultValue = "10") int size,
            @Parameter(description = "정렬 기준 필드 (예: createdAt)")
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @Parameter(description = "정렬 방식 (asc 또는 desc)")
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder) {

        Sort sort = Sort.by("desc".equals(sortOrder) ? Sort.Order.desc(sortBy) : Sort.Order.asc(sortBy));
        return ResponseEntity.ok(aiService.searchAiAnswersByKeyword(keyword, page, size, sort));
    }

    // AI 응답 숨김 처리
    @RoleCheck({UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @PatchMapping("/{aiId}/hide")
    @CurrentUserInject
    @Operation(summary = "AI 응답 숨김 처리", description = "특정 AI 응답을 숨깁니다.")
    @ApiResponse(responseCode = "200", description = "AI 응답 숨김 처리 성공")
    public ResponseEntity<AiResponseDto> hideAiAnswer(
            @Parameter(description = "숨길 AI 응답의 UUID")
            @PathVariable UUID aiId,
            @Parameter(hidden = true) @CurrentUser UserContext userContext
            ) {

        AiResponseDto response = aiService.hideAiAnswer(aiId, userContext);
        return ResponseEntity.ok(response); // 200 OK + 숨김 처리된 응답 반환
    }

    // AI 응답 복구 처리
    @RoleCheck({UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @PatchMapping("/{aiId}/restore")
    @CurrentUserInject
    @Operation(summary = "AI 응답 복구 처리", description = "숨겨진 AI 응답을 복구합니다.")
    @ApiResponse(responseCode = "200", description = "AI 응답 복구 성공")
    public ResponseEntity<AiResponseDto> restoreAiAnswer(
            @Parameter(description = "복구할 AI 응답의 UUID")
            @PathVariable UUID aiId,
            @Parameter(hidden = true) @CurrentUser UserContext userContext) {

        AiResponseDto response = aiService.restoreAiAnswer(aiId, userContext);
        return ResponseEntity.ok(response); // 200 OK + 복구된 응답 반환
    }
}
