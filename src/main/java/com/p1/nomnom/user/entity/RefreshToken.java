package com.p1.nomnom.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; // 사용자 식별자 (userId 대신 username 사용)

    @Column(nullable = false, length = 512)
    private String refreshToken; // Refresh Token 값

    @Column(nullable = false)
    private LocalDateTime expiryDate; // Refresh Token 만료 시간

    // Refresh Token 업데이트 (갱신 시 사용)
    public void updateToken(String newRefreshToken, LocalDateTime newExpiryDate) {
        this.refreshToken = newRefreshToken;
        this.expiryDate = newExpiryDate;
    }
}
