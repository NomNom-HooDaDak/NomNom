package com.p1.nomnom.user.controller;

import com.p1.nomnom.security.aop.RoleCheck;
import com.p1.nomnom.user.dto.RoleUpdateRequestDto;
import com.p1.nomnom.user.entity.UserRoleEnum;
import com.p1.nomnom.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Slf4j
public class AdminController {

    private final UserService userService;

    // MASTER 계정만 CUSTOMER의 ROLE을 OWNER 또는 MANAGER로 변경 가능
    @RoleCheck(UserRoleEnum.MASTER) // MASTER 계정만 실행 가능
    @PatchMapping("/role")
    public ResponseEntity<String> changeUserRole(@RequestBody RoleUpdateRequestDto requestDto) {
        userService.changeUserRole(requestDto);
        log.info("사용자 권한 변경 완료: 대상 사용자={}, 변경된 ROLE={}", requestDto.getUsername(), requestDto.getNewRole());
        return ResponseEntity.ok("사용자 권한이 " + requestDto.getNewRole() + "(으)로 변경되었습니다.");
    }
}
