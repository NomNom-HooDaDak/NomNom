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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    // 회원가입 로직
    @Transactional
    public void signup(SignupRequestDto requestDto) {
        String username = requestDto.getUsername();
        String email = requestDto.getEmail();
        String phone = requestDto.getPhone();
        String address = requestDto.getAddress();
        String password = requestDto.getPassword();
        String passwordCheck = requestDto.getPasswordCheck();

        // 비밀번호 일치 확인
        if (!password.equals(passwordCheck)) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인 값이 일치하지 않습니다.");
        }

        // 중복 검사
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다.");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        // 기본 권한 설정
        UserRoleEnum role = UserRoleEnum.CUSTOMER;

        // 사용자 저장
        User user = new User(username, email, encodedPassword, phone, role, false);
        userRepository.save(user);
    }

    // 로그인 로직 (AccessToken & RefreshToken 발급)
    @Transactional
    public String login(LoginRequestDto requestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.getUsername(),
                        requestDto.getPassword()
                )
        );

        // 사용자 정보 조회
        User user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // JWT 토큰 생성
        String accessToken = jwtUtil.createAccessToken(user.getUsername(), user.getRole());
        RefreshToken refreshToken = jwtUtil.createRefreshToken(user.getUsername());

        // RefreshToken 저장 (기존 토큰이 있다면 업데이트)
        refreshTokenRepository.save(refreshToken);

        return "Bearer " + accessToken;
    }
}
