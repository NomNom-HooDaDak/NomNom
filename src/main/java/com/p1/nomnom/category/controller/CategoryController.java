package com.p1.nomnom.category.controller;

import com.p1.nomnom.category.dto.request.CategoryRequestDTO;
import com.p1.nomnom.category.dto.response.CategoryResponseDTO;
import com.p1.nomnom.category.service.CategoryService;
import com.p1.nomnom.security.aop.RoleCheck;
import com.p1.nomnom.user.entity.UserRoleEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories") // 카테고리 관련 API 엔드포인트 정의
@Tag(name = "카테고리 API", description = "카테고리 관련 API")
public class CategoryController {

    @Autowired
    private CategoryService categoryService; // 카테고리 서비스 의존성 주입

    // 카테고리 생성 엔드포인트 (매니저, 마스터)
    @RoleCheck({UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @PostMapping
    @Operation(summary = "카테고리 생성", description = "새로운 카테고리를 생성합니다.")
    @ApiResponse(responseCode = "200", description = "카테고리 생성 성공")
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @Parameter(description = "생성할 카테고리 정보")
            @RequestBody CategoryRequestDTO categoryRequestDTO) {
        return ResponseEntity.ok(categoryService.createCategory(categoryRequestDTO));
    }

    // 서치 기능을 포함한 카테고리 조회 (모두 가능)
    @RoleCheck({UserRoleEnum.CUSTOMER, UserRoleEnum.OWNER, UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @GetMapping
    @Operation(summary = "카테고리 조회", description = "검색 기능을 포함한 카테고리 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "카테고리 목록 조회 성공")
    public ResponseEntity<List<CategoryResponseDTO>> getCategories(
            @Parameter(description = "한 페이지에 포함될 카테고리 개수")
            @RequestParam(defaultValue = "10") int pageSize,
            @Parameter(description = "정렬 기준 필드 (예: createdAt)")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "정렬 방식 (asc 또는 desc)")
            @RequestParam(defaultValue = "asc") String sortOrder,
            @Parameter(description = "검색할 카테고리 이름")
            @RequestParam(required = false) String name) {

        if (pageSize != 10 && pageSize != 30 && pageSize != 50) {
            pageSize = 10;
        }

        Sort sort = "desc".equals(sortOrder) ? Sort.by(Sort.Order.desc(sortBy)) : Sort.by(Sort.Order.asc(sortBy));
        List<CategoryResponseDTO> categories = categoryService.searchCategories(name, pageSize, sort);
        return ResponseEntity.ok(categories);
    }

    // 카테고리 수정 (ID 기반으로) 엔드포인트 (MANAGER, MASTER만 가능)
    @RoleCheck({UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @PatchMapping("/updateById/{categoryId}")
    @Operation(summary = "카테고리 수정", description = "ID를 기반으로 카테고리를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "카테고리 수정 성공")
    public ResponseEntity<CategoryResponseDTO> updateCategoryById(
            @Parameter(description = "수정할 카테고리의 UUID")
            @PathVariable UUID categoryId,
            @Parameter(description = "수정할 카테고리 정보")
            @RequestBody CategoryRequestDTO categoryRequestDTO) {
        return ResponseEntity.ok(categoryService.updateCategoryById(categoryId, categoryRequestDTO));
    }

    // 카테고리 숨기기 (삭제, ID 기반) 엔드포인트 (MANAGER, MASTER만 가능)
    @RoleCheck({UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @PatchMapping("/hideById/{categoryId}")
    @Operation(summary = "카테고리 숨기기", description = "ID를 기반으로 카테고리를 숨깁니다.")
    @ApiResponse(responseCode = "200", description = "카테고리 숨기기 성공")
    public ResponseEntity<CategoryResponseDTO> hideCategoryById(
            @Parameter(description = "숨길 카테고리의 UUID")
            @PathVariable UUID categoryId,
            @Parameter(description = "숨김 처리한 관리자 이름")
            @RequestParam String deletedBy) {
        return ResponseEntity.ok(categoryService.hideCategory(categoryId));
    }

    // 카테고리 복구 (Unhide) 엔드포인트 (MANAGER, MASTER만 가능)
    @RoleCheck({UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @PatchMapping("/unhide/{categoryId}")
    @Operation(summary = "카테고리 복구", description = "숨겨진 카테고리를 복구합니다.")
    @ApiResponse(responseCode = "200", description = "카테고리 복구 성공")
    public ResponseEntity<CategoryResponseDTO> unhideCategory(
            @Parameter(description = "복구할 카테고리의 UUID")
            @PathVariable UUID categoryId) {
        return ResponseEntity.ok(categoryService.unhideCategory(categoryId));
    }
}

