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
    public static final String REFRESH_TOKEN_HEADER = "Refresh-Token";
    public static final String AUTHORIZATION_KEY = "role";
    public static final String BEARER_PREFIX = "Bearer ";

    // JWT 만료시간 설정 (AccessToken: 15분, RefreshToken: 7일)
    private final long ACCESS_TOKEN_TIME = 15 * 60 * 1000L;
    private final long REFRESH_TOKEN_TIME = 7 * 24 * 60 * 60 * 1000L;

    @Value("${jwt.secret.key}")
    private String secretKey;
    private SecretKey key;

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
        log.info("createAccessToken() 호출됨 - username: {}, role: {}", username, role);

        String token = createToken(username, role, ACCESS_TOKEN_TIME);

        log.info("createAccessToken() 완료 - token: {}", token);
        return BEARER_PREFIX + token;
    }


    // Refresh Token 생성 (DB 저장용)
    public RefreshToken createRefreshToken(String username, UserRoleEnum role) {
        log.info("createRefreshToken() 호출됨 - username: {}, role: {}", username, role);

        String token = createToken(username, role, REFRESH_TOKEN_TIME);
        LocalDateTime expiryDate = LocalDateTime.now().plusDays(7);

        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUsername(username);
        if (existingToken.isPresent()) {
            log.info("기존 RefreshToken 업데이트: {}", existingToken.get().getRefreshToken());
            existingToken.get().updateToken(token, expiryDate);
            refreshTokenRepository.save(existingToken.get());
            return existingToken.get();
        } else {
            log.info("새로운 RefreshToken 생성: {}", token);
            RefreshToken newToken = RefreshToken.builder()
                    .username(username)
                    .refreshToken(token)
                    .expiryDate(expiryDate)
                    .build();
            refreshTokenRepository.save(newToken);
            return newToken;
        }
    }


    // JWT 생성
    public String createToken(String username, UserRoleEnum role, long expirationTime) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .subject(username)
                .claim(AUTHORIZATION_KEY, "ROLE_" + role.name())
                .issuedAt(now)
                .expiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 요청 헤더에서 JWT 추출 (Bearer 제거)
    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    // 요청에서 Refresh Token 추출 (Bearer 자동 제거)
    public String getRefreshTokenFromHeader(HttpServletRequest request) {
        String refreshToken = request.getHeader(REFRESH_TOKEN_HEADER);

        if (!StringUtils.hasText(refreshToken)) {
            return null;
        }

        return refreshToken.replace(BEARER_PREFIX, "");  // Bearer 제거 후 반환
    }

    // JWT 유효성 검사
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Refresh Token을 이용한 Access Token 재발급
    public String refreshAccessToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalArgumentException("유효한 Refresh Token이 필요합니다.");
        }

        refreshToken = refreshToken.replace(BEARER_PREFIX, "");  // Bearer 제거

        Optional<RefreshToken> storedToken = refreshTokenRepository.findByRefreshToken(refreshToken);
        if (storedToken.isEmpty()) {
            throw new IllegalArgumentException("DB에 해당 Refresh Token이 존재하지 않습니다.");
        }

        if (!validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token입니다.");
        }

        Claims claims = getUserInfoFromToken(refreshToken);
        String username = claims.getSubject();
        String role = claims.get(AUTHORIZATION_KEY, String.class);

        if (role == null) {
            throw new IllegalArgumentException("Refresh Token에서 사용자 역할을 찾을 수 없습니다.");
        }

        return createAccessToken(username, UserRoleEnum.valueOf(role.replace("ROLE_", "")));
    }

    // JWT에서 사용자 정보 추출
    public Claims getUserInfoFromToken(String token) {
        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
