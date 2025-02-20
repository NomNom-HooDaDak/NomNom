package com.p1.nomnom.category.service;

import com.p1.nomnom.category.dto.request.CategoryRequestDTO;
import com.p1.nomnom.category.dto.response.CategoryResponseDTO;
import com.p1.nomnom.security.aop.UserContext;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

public interface CategoryService {
    // 카테고리 생성
    CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO, UserContext userContext);

    // 카테고리 조회
    List<CategoryResponseDTO> getAllCategories();

    // 카테고리 숨김
    CategoryResponseDTO hideCategory(UUID categoryId, UserContext userContext);

    // 카테고리 복구
    CategoryResponseDTO unhideCategory(UUID categoryId, UserContext userContext);

    // 카테고리 이름으로 숨김 처리
    CategoryResponseDTO hideCategoryByName(String name, UserContext userContext);

    // 카테고리 삭제 (숨김 처리)
    void deleteCategoryByName(String name, UserContext userContext);

    // 카테고리 수정
    CategoryResponseDTO updateCategoryById(UUID categoryId, CategoryRequestDTO categoryRequestDTO, UserContext userContext);

    // 카테고리 검색
    List<CategoryResponseDTO> searchCategories(String name, int pageSize, Sort sort);
}
