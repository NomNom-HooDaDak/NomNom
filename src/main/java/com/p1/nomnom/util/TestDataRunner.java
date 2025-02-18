package com.p1.nomnom.util;

import com.p1.nomnom.security.jwt.JwtUtil;
import com.p1.nomnom.user.entity.RefreshToken;
import com.p1.nomnom.user.entity.User;
import com.p1.nomnom.user.entity.UserRoleEnum;
import com.p1.nomnom.user.repository.RefreshTokenRepository;
import com.p1.nomnom.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestDataRunner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public void run(ApplicationArguments args) {
        log.info("[TestDataRunner] 테스트 데이터 삽입 시작,,");

        // 1. 테스트 유저 10명 생성
        List<User> users = List.of(
                new User("testuser1", "test1@email.com", passwordEncoder.encode("Password1!"), "01012345671", UserRoleEnum.CUSTOMER, false),
                new User("testuser2", "test2@email.com", passwordEncoder.encode("Password2!"), "01012345672", UserRoleEnum.CUSTOMER, false),
                new User("testuser3", "test3@email.com", passwordEncoder.encode("Password3!"), "01012345673", UserRoleEnum.CUSTOMER, false),
                new User("testuser4", "test4@email.com", passwordEncoder.encode("Password4!"), "01012345674", UserRoleEnum.CUSTOMER, false),
                new User("testuser5", "test5@email.com", passwordEncoder.encode("Password5!"), "01012345675", UserRoleEnum.OWNER, false),
                new User("testuser6", "test6@email.com", passwordEncoder.encode("Password6!"), "01012345676", UserRoleEnum.OWNER, false),
                new User("testuser7", "test7@email.com", passwordEncoder.encode("Password7!"), "01012345677", UserRoleEnum.OWNER, false),
                new User("testuser8", "test8@email.com", passwordEncoder.encode("Password8!"), "01012345678", UserRoleEnum.MANAGER, false),
                new User("testuser9", "test9@email.com", passwordEncoder.encode("Password9!"), "01012345679", UserRoleEnum.MANAGER, false),
                new User("adminuser", "admin@email.com", passwordEncoder.encode("PassworD10!"), "01012345670", UserRoleEnum.MASTER, false)
        );

        for (User user : users) {
            User savedUser = userRepository.findByUsername(user.getUsername())
                    .orElseGet(() -> userRepository.save(user)); // 존재하지 않으면 저장

            // 2. AccessToken & RefreshToken 생성
            String accessToken = jwtUtil.createAccessToken(savedUser.getUsername(), savedUser.getRole());
            RefreshToken refreshToken = jwtUtil.createRefreshToken(savedUser.getUsername(), savedUser.getRole());

            // 3. RefreshToken 저장 (orElseGet 활용)
            refreshTokenRepository.findByUsername(savedUser.getUsername())
                    .orElseGet(() -> refreshTokenRepository.save(refreshToken));

            log.info("[TestDataRunner] 유저 생성 완료: username={}, role={}", savedUser.getUsername(), savedUser.getRole());
            log.info("AccessToken: {}", accessToken);
            log.info("RefreshToken: {}", refreshToken.getRefreshToken());
        }

        log.info("[TestDataRunner] 모든 테스트 데이터 삽입 완료!");
    }
}
