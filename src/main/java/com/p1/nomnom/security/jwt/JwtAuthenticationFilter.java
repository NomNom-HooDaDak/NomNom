package com.p1.nomnom.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.p1.nomnom.security.userdetails.UserDetailsImpl;
import com.p1.nomnom.user.dto.request.LoginRequestDto;
import com.p1.nomnom.user.entity.RefreshToken;
import com.p1.nomnom.user.entity.UserRoleEnum;
import com.p1.nomnom.user.repository.RefreshTokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@Slf4j(topic = "로그인 및 JWT 생성")
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    // 로그인 요청을 처리하는 엔드포인트 설정
    @Override
    public void setFilterProcessesUrl(String loginEndpoint) {
        super.setFilterProcessesUrl("/api/user/login");
    }

    // 로그인 인증 시 실행되는 메서드
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            log.info("요청 JSON 읽기 시작...");

            // 요청 본문을 한 번만 읽을 수 있도록 캐싱된 요청 사용
            String body = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            log.info("요청 본문 내용: {}", body);

            if (body.trim().isEmpty()) {
                log.error("요청 본문이 비어 있음");
                throw new RuntimeException("요청 본문이 비어 있습니다.");
            }

            // JSON 파싱
            LoginRequestDto requestDto = new ObjectMapper().readValue(body, LoginRequestDto.class);
            log.info("요청 JSON 파싱 완료 - username: {}", requestDto.getUsername());

            // 인증 토큰 생성
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword());

            return authenticationManager.authenticate(authenticationToken);
        } catch (IOException e) {
            log.error("요청 본문을 읽는 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("요청 본문을 읽는 중 오류 발생", e);
        }
    }

    // 인증 성공 시 실행되는 메서드
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        String username = ((UserDetailsImpl) authResult.getPrincipal()).getUsername();
        UserRoleEnum role = ((UserDetailsImpl) authResult.getPrincipal()).getUser().getRole();

        log.info("로그인 성공 - username: {}, role: {}", username, role);

        String accessToken = jwtUtil.createAccessToken(username, role);
        log.info("AccessToken 생성 완료: {}", accessToken);

        RefreshToken refreshToken = jwtUtil.createRefreshToken(username, role);
        log.info("RefreshToken 생성 완료: {}", refreshToken.getRefreshToken());

        refreshTokenRepository.save(refreshToken);

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);
        response.addHeader(JwtUtil.REFRESH_TOKEN_HEADER, refreshToken.getRefreshToken());

        log.info("JwtAuthenticationFilter - 다음 필터로 전달");
    }

    // 인증 실패 시 실행되는 메서드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(401);
        log.error("로그인 실패: {}", failed.getMessage());
    }
}
