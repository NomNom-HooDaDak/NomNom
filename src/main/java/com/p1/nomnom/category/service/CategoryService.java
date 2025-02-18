package com.p1.nomnom.category.service;

import com.p1.nomnom.category.dto.request.CategoryRequestDTO;
import com.p1.nomnom.category.dto.response.CategoryResponseDTO;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO);
    List<CategoryResponseDTO> getAllCategories();
    CategoryResponseDTO hideCategory(UUID categoryId);  //숨김 기능
    CategoryResponseDTO unhideCategory(UUID categoryId);  // 복구 기능

    // 이름으로 카테고리 숨김 처리
    CategoryResponseDTO hideCategoryByName(String name, String deletedBy);

    // 이름으로 카테고리 삭제 (숨김 처리)
    void deleteCategoryByName(String name);

    // ID로 카테고리 수정
    CategoryResponseDTO updateCategoryById(UUID categoryId, CategoryRequestDTO categoryRequestDTO);

    List<CategoryResponseDTO> searchCategories(String name, int pageSize, Sort sort);
}
