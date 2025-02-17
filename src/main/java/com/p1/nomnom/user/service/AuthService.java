package com.p1.nomnom.user.service;

import com.p1.nomnom.security.jwt.JwtUtil;
import com.p1.nomnom.user.dto.LoginRequestDto;
import com.p1.nomnom.user.dto.SignupRequestDto;
import com.p1.nomnom.user.entity.RefreshToken;
import com.p1.nomnom.user.entity.User;
import com.p1.nomnom.user.entity.UserRoleEnum;
import com.p1.nomnom.user.repository.RefreshTokenRepository;
import com.p1.nomnom.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Value("${admin.code}") // ADMIN 코드 환경 변수
    private String ADMIN_CODE;

    // 회원가입
    @Transactional
    public void signup(SignupRequestDto requestDto) {
        if (userRepository.findByUsername(requestDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        UserRoleEnum role = requestDto.getAdminCode() != null && requestDto.getAdminCode().equals(ADMIN_CODE)
                ? UserRoleEnum.MASTER
                : UserRoleEnum.CUSTOMER;

        User user = new User(
                requestDto.getUsername(),
                requestDto.getEmail(),
                passwordEncoder.encode(requestDto.getPassword()),
                requestDto.getPhone(),
                role,
                false
        );
        userRepository.save(user);
    }

    // 로그인
    @Transactional
    public String login(LoginRequestDto requestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword())
        );

        User user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return jwtUtil.createAccessToken(user.getUsername(), user.getRole());
    }

    // RefreshToken 생성 및 저장
    @Transactional
    public String generateAndSaveRefreshToken(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return jwtUtil.createRefreshToken(user.getUsername(), user.getRole()).getRefreshToken();
    }

    // RefreshToken을 이용한 AccessToken 재발급
    @Transactional
    public String refreshAccessToken(String refreshToken) {
        return jwtUtil.refreshAccessToken(refreshToken);
    }

}
