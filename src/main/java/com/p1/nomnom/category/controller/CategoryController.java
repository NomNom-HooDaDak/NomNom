package com.p1.nomnom.category.controller;

import com.p1.nomnom.category.dto.request.CategoryRequestDTO;
import com.p1.nomnom.category.dto.response.CategoryResponseDTO;
import com.p1.nomnom.category.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/nom/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@RequestBody CategoryRequestDTO categoryRequestDTO) {
        return ResponseEntity.ok(categoryService.createCategory(categoryRequestDTO));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    // 카테고리 숨기기 (ID 기반으로)
    @PatchMapping("/hideById/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> hideCategoryById(@PathVariable UUID categoryId, @RequestParam String deletedBy) {
        return ResponseEntity.ok(categoryService.hideCategory(categoryId)); // ID 기반으로 숨기기
    }

    @DeleteMapping("/deleteByName/{name}")
    public ResponseEntity<Void> deleteCategoryByName(@PathVariable String name) {
        categoryService.deleteCategoryByName(name);
        return ResponseEntity.noContent().build(); // 204 No Content 반환
    }

    // 카테고리 수정 엔드포인트 (ID 기반으로)
    @PatchMapping("/updateById/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> updateCategoryById(@PathVariable UUID categoryId, @RequestBody CategoryRequestDTO categoryRequestDTO) {
        return ResponseEntity.ok(categoryService.updateCategoryById(categoryId, categoryRequestDTO));
    }

    // 카테고리 복구 (Unhide) 엔드포인트
    @PatchMapping("/unhide/{categoryId}")
    public ResponseEntity<CategoryResponseDTO> unhideCategory(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(categoryService.unhideCategory(categoryId)); // 복구 처리
    }
}
