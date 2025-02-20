package com.p1.nomnom.category.controller;

import com.p1.nomnom.category.dto.request.CategoryRequestDTO;
import com.p1.nomnom.category.dto.response.CategoryResponseDTO;
import com.p1.nomnom.category.service.CategoryService;
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
@RequestMapping("/api/categories") // 카테고리 관련 API 엔드포인트 정의
@Tag(name = "카테고리 API", description = "카테고리 관련 API")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService; // 카테고리 서비스 의존성 주입

    // 카테고리 생성 엔드포인트 (매니저, 마스터)
    @RoleCheck({UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @PostMapping
    @CurrentUserInject
    @Operation(summary = "카테고리 생성", description = "새로운 카테고리를 생성합니다.")
    @ApiResponse(responseCode = "200", description = "카테고리 생성 성공")
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @Parameter(description = "생성할 카테고리 정보")
            @RequestBody CategoryRequestDTO categoryRequestDTO,
            @Parameter(hidden = true) @CurrentUser UserContext userContext) {
        return ResponseEntity.ok(categoryService.createCategory(categoryRequestDTO, userContext));
    }

    // 서치 기능을 포함한 카테고리 조회 (모두 가능)
    @RoleCheck({UserRoleEnum.CUSTOMER, UserRoleEnum.OWNER, UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @GetMapping
    @Operation(summary = "카테고리 조회", description = "검색 기능을 포함한 카테고리 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "카테고리 목록 조회 성공")
    public ResponseEntity<List<CategoryResponseDTO>> getCategories(
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
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
    @CurrentUserInject
    @Operation(summary = "카테고리 수정", description = "ID를 기반으로 카테고리를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "카테고리 수정 성공")
    public ResponseEntity<CategoryResponseDTO> updateCategoryById(
            @Parameter(description = "수정할 카테고리의 UUID")
            @PathVariable UUID categoryId,
            @Parameter(description = "수정할 카테고리 정보")
            @RequestBody CategoryRequestDTO categoryRequestDTO,
            @Parameter(hidden = true) @CurrentUser UserContext userContext) {
        return ResponseEntity.ok(categoryService.updateCategoryById(categoryId, categoryRequestDTO, userContext));
    }

    // 카테고리 숨기기 (삭제, ID 기반) 엔드포인트 (MANAGER, MASTER만 가능)
    @RoleCheck({UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @PatchMapping("/{categoryId}/hide")
    @CurrentUserInject
    @Operation(summary = "카테고리 숨기기", description = "ID를 기반으로 카테고리를 숨깁니다.")
    @ApiResponse(responseCode = "200", description = "카테고리 숨기기 성공")
    public ResponseEntity<CategoryResponseDTO> hideCategoryById(
            @Parameter(description = "숨길 카테고리의 UUID")
            @PathVariable UUID categoryId,
            @Parameter(hidden = true) @CurrentUser UserContext userContext) {
        return ResponseEntity.ok(categoryService.hideCategory(categoryId, userContext));
    }

    // 카테고리 복구 (Unhide) 엔드포인트 (MANAGER, MASTER만 가능)
    @RoleCheck({UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @PatchMapping("/{categoryId}/restore")
    @CurrentUserInject
    @Operation(summary = "카테고리 복구", description = "숨겨진 카테고리를 복구합니다.")
    @ApiResponse(responseCode = "200", description = "카테고리 복구 성공")
    public ResponseEntity<CategoryResponseDTO> unhideCategory(
            @Parameter(description = "복구할 카테고리의 UUID") @PathVariable UUID categoryId,
            @Parameter(hidden = true) @CurrentUser UserContext userContext) {
        return ResponseEntity.ok(categoryService.unhideCategory(categoryId, userContext));
    }
}
