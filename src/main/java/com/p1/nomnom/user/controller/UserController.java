package com.p1.nomnom.user.controller;

import com.p1.nomnom.security.jwt.JwtUtil;
import com.p1.nomnom.user.dto.LoginRequestDto;
import com.p1.nomnom.user.dto.LoginResponseDto;
import com.p1.nomnom.user.dto.SignupRequestDto;
import com.p1.nomnom.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    // 회원가입 API (ADMIN 코드 포함)
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        authService.signup(requestDto);
        log.info("회원가입 완료: username={}, email={}", requestDto.getUsername(), requestDto.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");
    }

    // 로그인 API (AccessToken & RefreshToken 발급)
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        log.info("로그인 시도: username={}", requestDto.getUsername());

        String accessToken = authService.login(requestDto);
        String refreshToken = authService.generateAndSaveRefreshToken(requestDto.getUsername());

        log.info("로그인 성공: username={}", requestDto.getUsername());
        log.info("AccessToken: {}", accessToken);
        log.info("RefreshToken: {}", refreshToken);

        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);
        response.addHeader(JwtUtil.REFRESH_TOKEN_HEADER, refreshToken);

        return ResponseEntity.ok(new LoginResponseDto(accessToken, refreshToken, "로그인 성공!"));
    }

    // AccessToken 재발급 API
    @PostMapping("/token/refresh")
    public ResponseEntity<LoginResponseDto> refreshAccessToken(HttpServletRequest request) {
        log.info("AccessToken 재발급 요청");

        String refreshToken = jwtUtil.getRefreshTokenFromHeader(request);

        // RefreshToken이 존재하지 않으면 에러 처리
        if (refreshToken == null || refreshToken.isBlank()) {
            log.error("요청에서 Refresh Token을 찾을 수 없습니다.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new LoginResponseDto(null, null, "Refresh Token이 필요합니다."));
        }

        try {
            String newAccessToken = authService.refreshAccessToken(refreshToken);

            log.info("AccessToken 재발급 완료: refreshToken={}", refreshToken);
            log.info("새 AccessToken: {}", newAccessToken);

            return ResponseEntity.ok(new LoginResponseDto(newAccessToken, refreshToken, "AccessToken이 재발급 되었습니다."));
        } catch (IllegalArgumentException e) {
            log.error("AccessToken 재발급 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponseDto(null, null, e.getMessage()));
        }
    }
}
