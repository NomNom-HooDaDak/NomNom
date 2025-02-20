package com.p1.nomnom.common.aop;

import com.p1.nomnom.user.entity.User;
import com.p1.nomnom.user.entity.UserRoleEnum;
import com.p1.nomnom.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CurrentUserAspect {

    private final UserRepository userRepository;

    @Around("@annotation(com.p1.nomnom.common.aop.CurrentUserInject) && execution(* *(.., @CurrentUser (*), ..))")
    public Object injectCurrentUser(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof UserContext) {  // UserContext 타입일 경우
                log.info("@CurrentUser 어노테이션이 있는 파라미터 인덱스: {}", i);

                // ✅ 현재 인증된 사용자 가져오기
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                if (principal instanceof UserDetails userDetails) {
                    String username = userDetails.getUsername();
                    log.info("인증된 사용자: {}", username);

                    // ✅ DB에서 유저 정보 가져오기
                    User user = userRepository.findByUsername(username)
                            .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + username));

                    // UserContext 객체에 정보 설정
                    UserContext userContext = new UserContext(user.getId(), user.getRole(), user.getUsername(), user);
                    args[i] = userContext;  // UserContext 객체 주입
                    log.info("파라미터에 UserContext를 주입했습니다: {}", userContext);
                } else {
                    throw new EntityNotFoundException("인증된 사용자가 없습니다.");
                }
            }
        }
        return joinPoint.proceed(args); // 변경된 파라미터로 메서드 실행
    }
}
