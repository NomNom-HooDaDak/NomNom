package com.p1.nomnom.user.controller;

import com.p1.nomnom.security.jwt.JwtUtil;
import com.p1.nomnom.user.dto.LoginRequestDto;
import com.p1.nomnom.user.dto.SignupRequestDto;
import com.p1.nomnom.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        authService.signup(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");
    }

    // 로그인 API (AccessToken & RefreshToken 발급)
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto requestDto, HttpServletRequest request, HttpServletResponse response) {
        String accessToken = authService.login(requestDto);

        // HttpServletRequest에서 RefreshToken 가져오기
        String refreshToken = jwtUtil.getRefreshTokenFromHeader(request);

        // 응답 헤더에 토큰 추가
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, accessToken);
        response.addHeader(JwtUtil.REFRESH_TOKEN_HEADER, refreshToken);

        return ResponseEntity.ok("로그인 성공!");
    }
}
