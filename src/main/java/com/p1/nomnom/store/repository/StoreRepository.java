package com.p1.nomnom.store.repository;

import com.p1.nomnom.store.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StoreRepository extends JpaRepository<Store, UUID> {

    // 검색 조건 (카테고리 ID와 페이지, 크기, 정렬기준)
    Page<Store> findAllByCategoryId(UUID categoryId, PageRequest pageRequest);

    // 기본 검색, 정렬 및 페이지 적용 메서드
    default Page<Store> searchStores(UUID categoryId, int page, int size, String sortBy) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy).descending()); // 생성일/수정일 기준 내림차순 정렬
        return findAllByCategoryId(categoryId, pageRequest);
    }

    // 이름을 포함한 검색 (카테고리와 함께)
    Page<Store> findByNameContainingAndCategoryId(String name, UUID categoryId, Pageable pageable);

    // 카테고리별로 모든 가게 조회
    Page<Store> findAllByCategoryId(UUID categoryId, Pageable pageable);

    // feature_ai 추가부분 - 기본 제공이라 제거 가능
    Optional<Store> findById(UUID storeId);

    @Query("SELECT s.name FROM Store s WHERE s.id = :storeId")
    Optional<String> findStoreNameById(@Param("storeId") UUID storeId);

    boolean existsByIdAndUserId(UUID storeId, Long userId);
}
