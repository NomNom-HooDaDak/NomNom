package com.p1.nomnom.user.repository;

import com.p1.nomnom.user.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUsername(String username); // username을 기준으로 토큰 조회
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    void deleteByUsername(String username); // 로그아웃 시 Refresh Token 삭제
}
