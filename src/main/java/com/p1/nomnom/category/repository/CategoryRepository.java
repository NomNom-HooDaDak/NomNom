package com.p1.nomnom.category.repository;

import com.p1.nomnom.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    // 이름으로 카테고리 삭제 메서드 추가
    void deleteByName(String name);

    // 이름 존재 여부 확인 (추가적인 유효성 검사를 위해 사용 가능)
    boolean existsByName(String name);

    // name으로 카테고리 조회
    Optional<Category> findByName(String name);

}