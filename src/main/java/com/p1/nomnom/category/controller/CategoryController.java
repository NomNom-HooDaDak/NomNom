package com.p1.nomnom.category.controller;

import com.p1.nomnom.category.dto.request.CategoryRequestDTO;
import com.p1.nomnom.category.dto.response.CategoryResponseDTO;
import com.p1.nomnom.category.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/nom/categories") // 카테고리 관련 API 엔드포인트 정의
public class CategoryController {

    @Autowired
    private CategoryService categoryService; // 카테고리 서비스 의존성 주입

    // 카테고리 생성 엔드포인트 (모두 가능)
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CategoryRequestDTO categoryRequestDTO) {
        return ResponseEntity.ok(categoryService.createCategory(categoryRequestDTO));
    }

    // 서치 기능을 포함한 카테고리 조회 (모두 가능)
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getCategories(
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String name) {

        if (pageSize != 10 && pageSize != 30 && pageSize != 50) {
            pageSize = 10;
        }

        Sort sort;
        if ("desc".equals(sortOrder)) {
            sort = Sort.by(Sort.Order.desc(sortBy)); // 내림차순 정렬
        } else {
            sort = Sort.by(Sort.Order.asc(sortBy)); // 오름차순 정렬
        }

        List<CategoryResponseDTO> categories = categoryService.searchCategories(name, pageSize, sort);
        return ResponseEntity.ok(categories);
    }

    // 카테고리 수정 (ID 기반으로) 엔드포인트 (MANAGER, MASTER만 가능)
    @PatchMapping("/updateById/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> updateCategoryById(@PathVariable UUID categoryId, @RequestBody CategoryRequestDTO categoryRequestDTO) {
        return ResponseEntity.ok(categoryService.updateCategoryById(categoryId, categoryRequestDTO));
    }

    // 카테고리 숨기기 (삭제, ID 기반) 엔드포인트 (MANAGER, MASTER만 가능)
    @PatchMapping("/hideById/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> hideCategoryById(@PathVariable UUID categoryId, @RequestParam String deletedBy) {
        return ResponseEntity.ok(categoryService.hideCategory(categoryId));
    }

    // 카테고리 복구 (Unhide) 엔드포인트 (MANAGER, MASTER만 가능)
    @PatchMapping("/unhide/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> unhideCategory(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(categoryService.unhideCategory(categoryId));
    }
}

