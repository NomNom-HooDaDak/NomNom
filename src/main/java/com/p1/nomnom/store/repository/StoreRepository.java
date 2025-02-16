package com.p1.nomnom.store.repository;

import com.p1.nomnom.store.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
