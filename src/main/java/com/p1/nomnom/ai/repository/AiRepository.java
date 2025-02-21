package com.p1.nomnom.ai.repository;

import com.p1.nomnom.ai.entity.Ai;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AiRepository extends JpaRepository<Ai, UUID> {
    //Page<Ai> findByQuestionContainingAndStoreId(String question, UUID storeId, Pageable pageable);

    Page<Ai> findAllByStoreId(UUID storeId, Pageable pageable);

    Page<Ai> findByKeywordContaining(String keyword, Pageable pageable);

    @Query("SELECT a FROM Ai a WHERE a.storeId = :storeId AND a.foodName = :foodName ORDER BY a.createdAt DESC")
    Ai findFirstAnswerByStoreAndFoodName(@Param("storeId") UUID storeId, @Param("foodName") String foodName);
}
