package com.p1.nomnom.common.aop;

import com.p1.nomnom.user.entity.User;
import com.p1.nomnom.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class CurrentUserAspect {

    private final UserRepository userRepository;

    public CurrentUserAspect(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Around("@annotation(com.p1.nomnom.common.aop.CurrentUserInject) && execution(* *(.., @CurrentUser (*), ..))")
    public Object injectCurrentUser(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof User) {
                log.info("🔍 Checking for @CurrentUser annotation on parameter index: {}", i);

                // ✅ 현재 인증된 사용자 가져오기
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (principal instanceof UserDetails userDetails) {
                    String username = userDetails.getUsername();
                    log.info("✅ Authenticated user: {}", username);

                    // ✅ DB에서 유저 조회
                    User user = userRepository.findByUsername(username)
                            .orElseThrow(() -> new EntityNotFoundException("❌ 사용자를 찾을 수 없습니다: " + username));

                    // ✅ 주입할 유저 설정
                    args[i] = user;
                    log.info("✅ Injected user into parameter: {}", user);
                } else {
                    throw new EntityNotFoundException("❌ 인증된 사용자가 없습니다.");
                }
            }
        }
        return joinPoint.proceed(args); // 변경된 파라미터로 메서드 실행
    }
}
