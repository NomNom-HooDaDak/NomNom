import com.p1.nomnom.ai.entity.Ai;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Repository
public interface AiRepository extends JpaRepository<Ai, UUID> {

    @Query("SELECT a FROM Ai a " +
            "WHERE (:foodName IS NULL OR a.foodName = :foodName) " +
            "AND (:startDate IS NULL OR a.createdAt >= :startDate) " +
            "AND (:endDate IS NULL OR a.createdAt <= :endDate) " +
            "AND (:storeId IS NULL OR a.storeId = :storeId) " +
            "AND (:keyword IS NULL OR a.keyword LIKE %:keyword%)")
    Page<Ai> findByFilters(Pageable pageable,
                           @Param("foodName") String foodName,
                           @Param("startDate") LocalDateTime startDate,
                           @Param("endDate") LocalDateTime endDate,
                           @Param("storeId") UUID storeId,
                           @Param("keyword") String keyword);
}
