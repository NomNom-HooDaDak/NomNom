package com.p1.nomnom.security.jwt;

import com.p1.nomnom.user.entity.RefreshToken;
import com.p1.nomnom.user.entity.UserRoleEnum;
import com.p1.nomnom.user.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

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

    @Value("${jwt.secret.key}")
    private String secretKey;
    private SecretKey key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    private final RefreshTokenRepository refreshTokenRepository;

    public JwtUtil(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // Access Token 생성
    public String createAccessToken(String username, UserRoleEnum role) {
        return BEARER_PREFIX + createToken(username, role.name(), ACCESS_TOKEN_TIME);
    }

    // Refresh Token 생성 (DB 저장용)
    public RefreshToken createRefreshToken(String username) {
        String token = BEARER_PREFIX + createToken(username, null, REFRESH_TOKEN_TIME);
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);

        // RefreshToken 저장 또는 갱신
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUsername(username);
        if (existingToken.isPresent()) {
            existingToken.get().updateToken(token, expiryDate);
            return refreshTokenRepository.save(existingToken.get());
        } else {
            RefreshToken newToken = RefreshToken.builder()
                    .username(username)
                    .refreshToken(token)
                    .expiryDate(expiryDate)
                    .build();
            return refreshTokenRepository.save(newToken);
        }
    }

    private String createToken(String username, String role, long expirationTime) {
        Claims claims = Jwts.claims().subject(username).build();
        if (role != null) {
            claims.put(AUTHORIZATION_KEY, role);
        }

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String getRefreshTokenFromHeader(HttpServletRequest request) {
        String refreshToken = request.getHeader(REFRESH_TOKEN_HEADER);
        if (StringUtils.hasText(refreshToken) && refreshToken.startsWith(BEARER_PREFIX)) {
            return refreshToken.substring(7);
        }
        return null;
    }

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

    // RefreshToken을 이용한 AccessToken 갱신
    public String refreshAccessToken(String refreshToken) {
        Optional<RefreshToken> storedToken = refreshTokenRepository.findByRefreshToken(refreshToken);
        if (storedToken.isEmpty() || !validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        Claims claims = getUserInfoFromToken(refreshToken);
        String username = claims.getSubject();
        String role = claims.get(AUTHORIZATION_KEY, String.class);

        return createAccessToken(username, UserRoleEnum.valueOf(role));
    }

    public Claims getUserInfoFromToken(String token) {
        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }

    public String getRoleFromToken(String token) {
        Claims claims = getUserInfoFromToken(token);
        return claims.get(AUTHORIZATION_KEY, String.class);
    }
}
