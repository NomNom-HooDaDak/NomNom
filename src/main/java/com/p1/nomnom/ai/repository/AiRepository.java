package com.p1.nomnom.ai.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.p1.nomnom.ai.entity.Ai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AiRepository extends JpaRepository<Ai, UUID> {
    // 질문을 포함하고 storeId로 검색하는 메소드
    Page<Ai> findByQuestionContainingAndStoreId(String question, UUID storeId, Pageable pageable);

    // storeId로만 검색하는 메소드
    Page<Ai> findAllByStoreId(UUID storeId, Pageable pageable);
}
