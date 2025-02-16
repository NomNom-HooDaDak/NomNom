package com.p1.nomnom.security.jwt;

import com.p1.nomnom.user.entity.UserRoleEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {
    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_TOKEN_HEADER = "Refresh-Token"; // Refresh Token을 위한 헤더
    public static final String AUTHORIZATION_KEY = "role"; // 사용자 권한
    public static final String BEARER_PREFIX = "Bearer ";

    // JWT 만료시간 설정 (AccessToken: 15분, RefreshToken: 7일)
    private final long ACCESS_TOKEN_TIME = 15 * 60 * 1000L;
    private final long REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60 * 1000L;

    @Value("${jwt.secret.key}") // Base64 Encode 한 SecretKey
    private String secretKey;
    private SecretKey key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // ✅ Access Token 생성 (Role 포함)
    public String createAccessToken(String username, UserRoleEnum role) {
        return BEARER_PREFIX + createToken(username, role.name(), ACCESS_TOKEN_TIME);
    }

    // ✅ Refresh Token 생성 (Role 없음)
    public String createRefreshToken(String username) {
        return BEARER_PREFIX + createToken(username, null, REFRESH_TOKEN_TIME);
    }

    // ✅ 공통 토큰 생성 메서드
    private String createToken(String username, String role, long expirationTime) {
        Claims claims = Jwts.claims().subject(username).build();
        if (role != null) {
            claims.put(AUTHORIZATION_KEY, role);
        }

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date()) // 발급일
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // 만료 시간
                .signWith(key, signatureAlgorithm) // 서명
                .compact();
    }

    // ✅ 헤더에서 JWT 가져오기
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // ✅ 헤더에서 Refresh Token 가져오기
    public String getRefreshTokenFromHeader(HttpServletRequest request) {
        String refreshToken = request.getHeader(REFRESH_TOKEN_HEADER);
        if (StringUtils.hasText(refreshToken) && refreshToken.startsWith(BEARER_PREFIX)) {
            return refreshToken.substring(7);
        }
        return null;
    }

    // ✅ 토큰 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token, 만료된 JWT 토큰입니다.");
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token, 유효하지 않은 JWT 토큰입니다.");
            return false;
        }
    }

    // ✅ Access Token이 만료되었을 때 Refresh Token으로 새로운 Access Token 발급
    public String refreshAccessToken(String refreshToken) {
        if (!validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        Claims claims = getUserInfoFromToken(refreshToken);
        String username = claims.getSubject();
        String role = claims.get(AUTHORIZATION_KEY, String.class);

        return createAccessToken(username, UserRoleEnum.valueOf(role));
    }

    // ✅ 토큰에서 사용자 정보 가져오기
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    // ✅ 토큰에서 Role 정보 가져오기
    public String getRoleFromToken(String token) {
        Claims claims = getUserInfoFromToken(token);
        return claims.get(AUTHORIZATION_KEY, String.class);
    }
}
