package com.p1.nomnom.store.controller;

import com.p1.nomnom.security.aop.RoleCheck;
import com.p1.nomnom.store.dto.request.StoreRequestDTO;
import com.p1.nomnom.store.dto.response.StoreResponseDTO;
import com.p1.nomnom.store.service.StoreService;
import com.p1.nomnom.user.entity.UserRoleEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid; // jakarta.validation 패키지로 변경
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
@Tag(name = "가게 API", description = "가게 관련 API")
public class StoreController {

    private final StoreService storeService;

    // 가게 등록, 가게 주인은 못함
    @RoleCheck({UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @PostMapping
    @Operation(summary = "가게 등록", description = "새로운 가게를 등록합니다. 가게 주인은 등록할 수 없습니다.")
    @ApiResponse(responseCode = "200", description = "가게 등록 성공")
    public ResponseEntity<StoreResponseDTO> createStore(
            @Parameter(description = "등록할 가게 정보")
            @Valid @RequestBody StoreRequestDTO storeRequestDTO) {
        StoreResponseDTO storeResponseDTO = storeService.createStore(storeRequestDTO);
        return ResponseEntity.ok(storeResponseDTO);
    }

    // 가게 정보 수정
    @RoleCheck({UserRoleEnum.OWNER, UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @PatchMapping("/{storeId}")
    @Operation(summary = "가게 정보 수정", description = "기존 가게의 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "가게 수정 성공")
    public ResponseEntity<StoreResponseDTO> updateStore(
            @Parameter(description = "수정할 가게의 UUID")
            @PathVariable UUID storeId,
            @Parameter(description = "수정할 가게 정보")
            @Valid @RequestBody StoreRequestDTO storeRequestDTO) {
        StoreResponseDTO storeResponseDTO = storeService.updateStore(storeId, storeRequestDTO);
        return ResponseEntity.ok(storeResponseDTO);
    }


    // 특정 가게 조회
    @RoleCheck({UserRoleEnum.CUSTOMER, UserRoleEnum.OWNER, UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @GetMapping("/{storeId}")
    @Operation(summary = "특정 가게 조회", description = "특정 가게의 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "가게 조회 성공")
    public ResponseEntity<StoreResponseDTO> getStore(
            @Parameter(description = "조회할 가게의 UUID")
            @PathVariable UUID storeId) {
        StoreResponseDTO storeResponseDTO = storeService.getStore(storeId);
        return ResponseEntity.ok(storeResponseDTO);
    }

    // 카테고리별 가게 조회 + 검색, 정렬, 페이지네이션
    @RoleCheck({UserRoleEnum.CUSTOMER, UserRoleEnum.OWNER, UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @GetMapping("/category")
    @Operation(summary = "카테고리별 가게 조회", description = "카테고리별로 가게를 조회하며, 검색, 정렬, 페이지네이션 기능을 제공합니다.")
    @ApiResponse(responseCode = "200", description = "카테고리별 가게 조회 성공")
    public ResponseEntity<List<StoreResponseDTO>> getStoresByCategory(
            @Parameter(description = "조회할 카테고리의 UUID")
            @RequestParam(value = "category_id") UUID categoryId,
            @Parameter(description = "페이지 번호 (기본값 0)")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "페이지 크기 (기본값 10)")
            @RequestParam(value = "size", defaultValue = "10") int size,
            @Parameter(description = "정렬 기준 필드 (예: createdAt)")
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @Parameter(description = "정렬 방식 (asc 또는 desc)")
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder) {

        Sort sort = "desc".equals(sortOrder) ? Sort.by(Sort.Order.desc(sortBy)) : Sort.by(Sort.Order.asc(sortBy));
        List<StoreResponseDTO> storeList = storeService.getStoresByCategory(categoryId, page, size);
        return ResponseEntity.ok(storeList);
    }

    // 모든 가게 조회 + 검색, 정렬, 페이지네이션
    @RoleCheck({UserRoleEnum.CUSTOMER, UserRoleEnum.OWNER, UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @GetMapping
    @Operation(summary = "모든 가게 조회", description = "전체 가게를 조회하며, 검색, 정렬, 페이지네이션 기능을 제공합니다.")
    @ApiResponse(responseCode = "200", description = "전체 가게 조회 성공")
    public ResponseEntity<List<StoreResponseDTO>> getAllStores(
            @Parameter(description = "페이지 번호 (기본값 0)")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "페이지 크기 (기본값 10)")
            @RequestParam(value = "size", defaultValue = "10") int size,
            @Parameter(description = "정렬 기준 필드 (예: createdAt)")
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @Parameter(description = "정렬 방식 (asc 또는 desc)")
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder) {

        Sort sort = "desc".equals(sortOrder) ? Sort.by(Sort.Order.desc(sortBy)) : Sort.by(Sort.Order.asc(sortBy));
        List<StoreResponseDTO> storeList = storeService.getAllStores(page, size);
        return ResponseEntity.ok(storeList);
    }

    // 가게 숨김 처리
    @RoleCheck({UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @PatchMapping("/{storeId}/hide")
    @Operation(summary = "가게 숨김 처리", description = "가게를 숨깁니다.")
    @ApiResponse(responseCode = "200", description = "가게 숨김 처리 성공")
    public ResponseEntity<StoreResponseDTO> hideStore(
            @Parameter(description = "숨길 가게의 UUID")
            @PathVariable UUID storeId) {
        StoreResponseDTO storeResponseDTO = storeService.hideStore(storeId);
        return ResponseEntity.ok(storeResponseDTO);
    }

    //가게 복구
    @RoleCheck({UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @PatchMapping("/{storeId}/restore")
    @Operation(summary = "가게 복구", description = "숨겨진 가게를 복구합니다.")
    @ApiResponse(responseCode = "200", description = "가게 복구 성공")
    public ResponseEntity<StoreResponseDTO> restoreStore(
            @Parameter(description = "복구할 가게의 UUID")
            @PathVariable UUID storeId) {
        StoreResponseDTO storeResponseDTO = storeService.restoreStore(storeId);
        return ResponseEntity.ok(storeResponseDTO);
    }
}
