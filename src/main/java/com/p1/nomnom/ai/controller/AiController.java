package com.p1.nomnom.ai.controller;

import com.p1.nomnom.ai.dto.request.AiRequestDto;
import com.p1.nomnom.ai.dto.response.AiResponseDto;
import com.p1.nomnom.ai.service.AiService;
import com.p1.nomnom.security.aop.RoleCheck;
import com.p1.nomnom.user.entity.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    // AI 상품 설명 자동 생성
    @RoleCheck({UserRoleEnum.OWNER, UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @PostMapping("/foods/description")
    public AiResponseDto generateFoodDescription(@RequestBody AiRequestDto requestDto) {
        return aiService.getAiAnswer(requestDto);
    }

    // 모든 AI 응답 조회 (검색 + 페이지네이션 + 정렬)
    @RoleCheck({UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @GetMapping("/all")
    public ResponseEntity<List<AiResponseDto>> getAllAiAnswers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder) {

        Sort sort = Sort.by("desc".equals(sortOrder) ? Sort.Order.desc(sortBy) : Sort.Order.asc(sortBy));
        return ResponseEntity.ok(aiService.getAllAiAnswers(page, size, sort));
    }

    // 특정 가게의 AI 응답 조회
    @RoleCheck({UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<AiResponseDto>> getAiAnswersByStore(
            @PathVariable UUID storeId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder) {

        Sort sort = Sort.by("desc".equals(sortOrder) ? Sort.Order.desc(sortBy) : Sort.Order.asc(sortBy));
        return ResponseEntity.ok(aiService.getAiAnswersByStore(storeId, page, size, sort));
    }

    // 특정 키워드로 AI 응답 검색
    @RoleCheck({UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @GetMapping("/search")
    public ResponseEntity<List<AiResponseDto>> searchAiAnswersByKeyword(
            @RequestParam String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder) {

        Sort sort = Sort.by("desc".equals(sortOrder) ? Sort.Order.desc(sortBy) : Sort.Order.asc(sortBy));
        return ResponseEntity.ok(aiService.searchAiAnswersByKeyword(keyword, page, size, sort));
    }

    // AI 응답 숨김 처리
    @RoleCheck({UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @PatchMapping("/{aiId}/hide")
    public ResponseEntity<AiResponseDto> hideAiAnswer(
            @PathVariable UUID aiId,
            @RequestParam String deletedBy) {

        AiResponseDto response = aiService.hideAiAnswer(aiId, deletedBy);

        return ResponseEntity.ok(response); // 200 OK + 숨김 처리된 응답 반환
    }

    // AI 응답 복구 처리
    @RoleCheck({UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @PatchMapping("/{aiId}/restore")
    public ResponseEntity<AiResponseDto> restoreAiAnswer(
            @PathVariable UUID aiId,
            @RequestParam String updatedBy) {

        AiResponseDto response = aiService.restoreAiAnswer(aiId, updatedBy);

        return ResponseEntity.ok(response); // 200 OK + 복구된 응답 반환
    }
}
