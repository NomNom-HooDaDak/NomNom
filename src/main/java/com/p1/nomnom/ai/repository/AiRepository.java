package com.p1.nomnom.ai.repository;

import com.p1.nomnom.ai.entity.Ai;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AiRepository extends JpaRepository<Ai, UUID> {
    //Page<Ai> findByQuestionContainingAndStoreId(String question, UUID storeId, Pageable pageable);

    Page<Ai> findAllByStoreId(UUID storeId, Pageable pageable);

    Page<Ai> findByKeywordContaining(String keyword, Pageable pageable);
}
