package com.p1.nomnom.category.service;

import com.p1.nomnom.category.dto.request.CategoryRequestDTO;
import com.p1.nomnom.category.dto.response.CategoryResponseDTO;
import com.p1.nomnom.category.entity.Category;
import com.p1.nomnom.category.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public CategoryResponseDTO createCategory(CategoryRequestDTO categoryRequestDTO) {
        Category category = new Category();
        category.setName(categoryRequestDTO.getName());
        category.setDescription(categoryRequestDTO.getDescription());
        category = categoryRepository.save(category);

        return mapToResponseDTO(category);
    }

    @Override
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponseDTO hideCategory(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.hide("admin");
        return mapToResponseDTO(categoryRepository.save(category));
    }

    @Override
    public CategoryResponseDTO unhideCategory(UUID categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        category.restoreCategory("admin");  // restoreCategory() 호출하여 복구
        return mapToResponseDTO(categoryRepository.save(category));
    }

    @Override
    public CategoryResponseDTO hideCategoryByName(String name, String deletedBy) {
        Category category = categoryRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Category with name '" + name + "' not found"));
        category.hide(deletedBy);
        return mapToResponseDTO(categoryRepository.save(category));
    }

    @Override
    public void deleteCategoryByName(String name) {
        Category category = categoryRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Category with name '" + name + "' not found"));
        category.hide("admin");
        categoryRepository.save(category);
    }

    @Override
    public CategoryResponseDTO updateCategoryById(UUID categoryId, CategoryRequestDTO categoryRequestDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category with id '" + categoryId + "' not found"));
        category.setName(categoryRequestDTO.getName());
        category.setDescription(categoryRequestDTO.getDescription());

        return mapToResponseDTO(categoryRepository.save(category));
    }

    private CategoryResponseDTO mapToResponseDTO(Category category) {
        CategoryResponseDTO responseDTO = new CategoryResponseDTO();
        responseDTO.setId(category.getId());
        responseDTO.setName(category.getName());
        responseDTO.setDescription(category.getDescription());
        responseDTO.setHidden(category.getHidden());
        responseDTO.setCreatedAt(category.getCreatedAt());
        responseDTO.setUpdatedAt(category.getUpdatedAt());
        responseDTO.setDeletedAt(category.getDeletedAt());
        responseDTO.setDeletedBy(category.getDeletedBy());
        return responseDTO;
    }
}
