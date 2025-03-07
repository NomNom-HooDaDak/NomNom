package com.p1.nomnom.user.controller;

import com.p1.nomnom.security.jwt.JwtUtil;
import com.p1.nomnom.user.dto.request.LoginRequestDto;
import com.p1.nomnom.user.dto.response.LoginResponseDto;
import com.p1.nomnom.user.dto.request.SignupRequestDto;
import com.p1.nomnom.user.dto.response.SignupResponseDto;
import com.p1.nomnom.user.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "사용자(CUSTOMER) API", description = "사용자 관련 API")
public class UserController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    // 회원가입 API (ADMIN 코드 포함)
    @Operation(summary = "회원가입", description = "회원가입 합니다.")
    @ApiResponse(responseCode = "200", description = "회원가입 성공")
    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        SignupResponseDto responseDto = authService.signup(requestDto);
        log.info("회원가입 완료: username={}, email={}", requestDto.getUsername(), requestDto.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 로그인 API (AccessToken & RefreshToken 발급)
    @Operation(summary = "로그인", description = "로그인 합니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto, HttpServletResponse response) {
        log.info("UserController - 로그인 API 호출됨: username={}", requestDto.getUsername());

        try {
            LoginResponseDto loginResponse = authService.login(requestDto);

            log.info("AccessToken: {}", loginResponse.getAccessToken());
            log.info("RefreshToken: {}", loginResponse.getRefreshToken());

            response.addHeader(JwtUtil.AUTHORIZATION_HEADER, loginResponse.getAccessToken());
            response.addHeader(JwtUtil.REFRESH_TOKEN_HEADER, loginResponse.getRefreshToken());

            log.info("로그인 성공 - Response 반환 준비 완료");
            return ResponseEntity.ok(loginResponse);
        } catch (Exception e) {
            log.error("로그인 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponseDto(null, null, "로그인 실패: " + e.getMessage()));
        }
    }



    // AccessToken 재발급 API
    @Operation(summary = "Access토큰 재발급", description = "토큰이 만료되어 새로운 토큰 발급")
    @ApiResponse(responseCode = "200", description = "토큰 재발급 성공")
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
