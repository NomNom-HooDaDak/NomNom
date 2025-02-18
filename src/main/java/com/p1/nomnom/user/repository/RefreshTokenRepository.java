package com.p1.nomnom.user.repository;

import com.p1.nomnom.user.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUsername(String username);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    void deleteByUsername(String username);
}
