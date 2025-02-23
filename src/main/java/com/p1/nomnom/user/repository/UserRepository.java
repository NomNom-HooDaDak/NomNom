package com.p1.nomnom.user.repository;

import com.p1.nomnom.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    @Query("SELECT COALESCE(MAX(u.id), 0) FROM User u")
    Long getMaxUserId();

    @Query(value = "SELECT setval('p_users_id_seq', :newStartValue, false)", nativeQuery = true)
    void resetSequence(@Param("newStartValue") Long newStartValue);
}
