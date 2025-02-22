package com.p1.nomnom.category.service;

import com.p1.nomnom.category.dto.request.CategoryRequestDTO;
import com.p1.nomnom.category.dto.response.CategoryResponseDTO;
import com.p1.nomnom.category.entity.Category;
import com.p1.nomnom.category.repository.CategoryRepository;
import com.p1.nomnom.security.aop.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    // 카테고리 생성
    @Override
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO, UserContext userContext) {
        Category category = new Category();
        category.setName(categoryRequestDTO.getName());
        category.setDescription(categoryRequestDTO.getDescription());
        category.setCreatedBy(userContext.getUsername());

        categoryRepository.save(category);

        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getHidden(),
                category.getCreatedAt(),
                category.getUpdatedAt(),
                category.getDeletedAt(),
                category.getDeletedBy()
        );
    }

    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(category -> new CategoryResponseDTO(
                        category.getId(),
                        category.getName(),
                        category.getDescription(),
                        category.getHidden(),
                        category.getCreatedAt(),
                        category.getUpdatedAt(),
                        category.getDeletedAt(),
                        category.getDeletedBy()
                ))
                .collect(Collectors.toList());
    }

    // 카테고리 숨김 처리
    @Override
    @Transactional
    public CategoryResponseDTO hideCategory(UUID categoryId, UserContext userContext) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.hide(userContext.getUsername());
        categoryRepository.save(category);

        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getHidden(),
                category.getCreatedAt(),
                category.getUpdatedAt(),
                category.getDeletedAt(),
                category.getDeletedBy()
        );
    }

    // 카테고리 복구 처리
    @Override
    @Transactional
    public CategoryResponseDTO unhideCategory(UUID categoryId, UserContext userContext) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.restoreCategory(userContext.getUsername());
        categoryRepository.save(category);

        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getHidden(),
                category.getCreatedAt(),
                category.getUpdatedAt(),
                category.getDeletedAt(),
                category.getDeletedBy()
        );
    }

    // 카테고리 이름으로 숨김 처리
    @Override
    @Transactional
    public CategoryResponseDTO hideCategoryByName(String name, UserContext userContext) {
        Category category = categoryRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Category with name '" + name + "' not found"));

        category.hide(userContext.getUsername());
        categoryRepository.save(category);

        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getHidden(),
                category.getCreatedAt(),
                category.getUpdatedAt(),
                category.getDeletedAt(),
                category.getDeletedBy()
        );
    }

    // 카테고리 삭제 처리 (숨김)
    @Override
    @Transactional
    public void deleteCategoryByName(String name, UserContext userContext) {
        Category category = categoryRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Category with name '" + name + "' not found"));

        category.hide(userContext.getUsername());
        categoryRepository.save(category);
    }

    // 카테고리 수정
    @Override
    @Transactional
    public CategoryResponseDTO updateCategoryById(UUID categoryId, CategoryRequestDTO categoryRequestDTO, UserContext userContext) {
        // 1. 카테고리 존재 여부 확인
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // 2. 수정할 데이터 반영
        category.setName(categoryRequestDTO.getName());
        category.setDescription(categoryRequestDTO.getDescription());

        // 3. 업데이트한 유저 저장 (NULL 방지)
        String updatedBy = userContext.getUsername();
        if (updatedBy != null) {
            category.setUpdatedBy(updatedBy);
        } else {
            category.setUpdatedBy("SYSTEM");  // 만약 null이면 기본값을 SYSTEM으로 설정
        }

        // 4. 카테고리 저장
        Category updatedCategory = categoryRepository.save(category);

        // 5. 저장된 데이터 검증 로그 (콘솔에서 확인 가능)
        System.out.println("Updated Category: " + updatedCategory);

        // 6. 응답 DTO 생성하여 반환
        return new CategoryResponseDTO(
                updatedCategory.getId(),
                updatedCategory.getName(),
                updatedCategory.getDescription(),
                updatedCategory.getHidden(),
                updatedCategory.getCreatedAt(),
                updatedCategory.getUpdatedAt(),
                updatedCategory.getDeletedAt(),
                updatedCategory.getDeletedBy()
        );
    }

    //  카테고리 검색 기능 구현
    @Override
    public List<CategoryResponseDTO> searchCategories(String name, int pageSize, Sort sort) {
        Pageable pageable = PageRequest.of(0, pageSize, sort);
        Page<Category> pageResult;

        if (name != null && !name.isEmpty()) {
            pageResult = categoryRepository.findByNameContaining(name, pageable);
        } else {
            pageResult = categoryRepository.findAll(pageable);
        }

        return pageResult.stream()
                .map(category -> new CategoryResponseDTO(
                        category.getId(),
                        category.getName(),
                        category.getDescription(),
                        category.getHidden(),
                        category.getCreatedAt(),
                        category.getUpdatedAt(),
                        category.getDeletedAt(),
                        category.getDeletedBy()
                ))
                .collect(Collectors.toList());
    }
}
