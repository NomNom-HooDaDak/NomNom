package com.p1.nomnom.user.service;

import com.p1.nomnom.user.dto.request.RoleUpdateRequestDto;
import com.p1.nomnom.user.entity.User;
import com.p1.nomnom.user.entity.UserRoleEnum;
import com.p1.nomnom.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void changeUserRole(RoleUpdateRequestDto requestDto) {
        // 변경할 사용자 조회
        User user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // CUSTOMER만 OWNER 또는 MANAGER로 변경 가능
        if (user.getRole() != UserRoleEnum.CUSTOMER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 OWNER 또는 MANAGER인 사용자는 변경할 수 없습니다.");
        }

        // 새로운 권한으로 변경
        UserRoleEnum newRole = UserRoleEnum.valueOf(requestDto.getNewRole());
        user.setRole(newRole);
        userRepository.save(user);
        log.info("사용자 {}의 권한이 {}로 변경되었습니다.", user.getUsername(), newRole);
    }
}
