package com.p1.nomnom.user.service;

import com.p1.nomnom.address.entity.Address;
import com.p1.nomnom.address.repository.AddressRepository;
import com.p1.nomnom.security.jwt.JwtUtil;
import com.p1.nomnom.user.dto.request.LoginRequestDto;
import com.p1.nomnom.user.dto.response.LoginResponseDto;
import com.p1.nomnom.user.dto.request.SignupRequestDto;
import com.p1.nomnom.user.dto.response.SignupResponseDto;
import com.p1.nomnom.user.entity.User;
import com.p1.nomnom.user.entity.UserRoleEnum;
import com.p1.nomnom.user.repository.RefreshTokenRepository;
import com.p1.nomnom.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final AddressRepository addressRepository;

    @Value("${admin.code}") // ADMIN 코드 환경 변수
    private String ADMIN_CODE;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void resetUserSequence() {
        Long maxId = userRepository.getMaxUserId();
        if (maxId == null) {
            maxId = 0L;
        }
        userRepository.resetSequence(maxId + 1);
    }

    // 회원가입
    @Transactional
    public SignupResponseDto signup(SignupRequestDto requestDto) {
        resetUserSequence();

        if (userRepository.findByUsername(requestDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자입니다.");
        }

        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        if (!requestDto.getPassword().equals(requestDto.getPasswordCheck())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        UserRoleEnum role = (requestDto.getAdminCode() != null && requestDto.getAdminCode().equals(ADMIN_CODE))
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

        if (requestDto.getAddress() != null && !requestDto.getAddress().isEmpty()) {
            saveUserAddress(user, requestDto.getAddress());
        }
        return new SignupResponseDto(user.getUsername(), "회원가입이 완료되었습니다.");
    }

    @Transactional
    public void saveUserAddress(User user, String addressValue) {
        Address address = new Address(UUID.randomUUID(), user, addressValue, true, false);
        addressRepository.save(address);
    }

    // 로그인
    @Transactional
    public LoginResponseDto login(LoginRequestDto requestDto) {
        log.info("AuthService - 로그인 진행 중: username={}", requestDto.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword())
            );
            log.info("인증 성공: {}", authentication.getName());

            User user = userRepository.findByUsername(requestDto.getUsername())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

            log.info("AccessToken 생성 시작...");
            String accessToken = jwtUtil.createAccessToken(user.getUsername(), user.getRole());
            log.info("AccessToken 생성 완료: {}", accessToken);

            log.info("RefreshToken 생성 시작...");
            String refreshToken = generateAndSaveRefreshToken(user.getUsername());
            log.info("RefreshToken 생성 완료: {}", refreshToken);

            return new LoginResponseDto(accessToken, refreshToken, "로그인이 성공적으로 완료되었습니다.");
        } catch (Exception e) {
            log.error("로그인 실패: {}", e.getMessage(), e);
            throw new RuntimeException("로그인 인증 과정에서 문제가 발생했습니다.", e);
        }
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
