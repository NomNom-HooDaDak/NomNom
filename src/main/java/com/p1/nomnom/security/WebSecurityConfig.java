package com.p1.nomnom.security;

import com.p1.nomnom.security.jwt.JwtUtil;
import com.p1.nomnom.security.jwt.JwtAuthenticationFilter;
import com.p1.nomnom.security.jwt.JwtAuthorizationFilter;
import com.p1.nomnom.security.userdetails.UserDetailsServiceImpl;
import com.p1.nomnom.user.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity // Spring Security 활성화
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final RefreshTokenRepository refreshTokenRepository;

    // 비밀번호 암호화 설정
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // AuthenticationManager 설정
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Spring Security 설정
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()); // CSRF 비활성화 (JWT 방식 사용)

        // 세션 관리 - STATELESS 모드 (JWT 사용 시 필수)
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 권한 설정
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // 정적 리소스 접근 허용
                .requestMatchers("/api/user/login", "/api/user/signup", "/api/user/token/refresh").permitAll()
                .requestMatchers("/api/customer/**").hasRole("CUSTOMER") // 고객 전용 API
                .requestMatchers("/api/owner/**").hasRole("OWNER") // 가게 사장 전용 API
                .requestMatchers("/api/manager/**").hasRole("MANAGER") // 관리자 전용 API
                .requestMatchers("/api/master/**").hasRole("MASTER") // 최상위 관리자(ADMIN) 전용 API
                .requestMatchers("/doc/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
        );

        // CORS 설정 추가
        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(List.of("*"));
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Refresh-Token"));
            return config;
        }));

        // 필터 설정
        http.addFilterBefore(new JwtAuthenticationFilter(authenticationManager(), jwtUtil, refreshTokenRepository),
                UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(new JwtAuthorizationFilter(jwtUtil, userDetailsService, refreshTokenRepository),
                UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}
