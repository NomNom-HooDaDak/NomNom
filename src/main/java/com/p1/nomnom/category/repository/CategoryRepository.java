package com.p1.nomnom.category.repository;

import com.p1.nomnom.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    // 이름으로 카테고리 찾기 (Optional 반환)
    Optional<Category> findByName(String name);

    // 이름에 포함된 값을 기준으로 카테고리 찾기
    Page<Category> findByNameContaining(String name, Pageable pageable);
}
