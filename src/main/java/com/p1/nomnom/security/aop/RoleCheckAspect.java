package com.p1.nomnom.security.aop;

import com.p1.nomnom.security.jwt.JwtUtil;
import com.p1.nomnom.user.entity.User;
import com.p1.nomnom.user.entity.UserRoleEnum;
import com.p1.nomnom.user.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class RoleCheckAspect {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final HttpServletRequest request;

    @Around("@annotation(roleCheck)") // @RoleCheck가 붙은 메서드 실행 전에 AOP 동작
    public Object checkRole(ProceedingJoinPoint joinPoint, RoleCheck roleCheck) throws Throwable {
        String token = jwtUtil.getJwtFromHeader(request);
        if (token == null || !jwtUtil.validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
        }

        Claims claims = jwtUtil.getUserInfoFromToken(token);
        String username = claims.getSubject();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        UserRoleEnum userRole = user.getRole();
        for (UserRoleEnum requiredRole : roleCheck.value()) {
            if (userRole == requiredRole) {
                log.info("접근 허용: {} (요구 역할: {})", userRole, requiredRole);
                return joinPoint.proceed(); // 역할이 맞으면 API 실행
            }
        }

        log.warn("접근 거부: {} (요구 역할: {})", userRole, roleCheck.value());
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.");
    }
}
