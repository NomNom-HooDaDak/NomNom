package com.p1.nomnom.security.aop;


import com.p1.nomnom.user.entity.User;
import com.p1.nomnom.user.entity.UserRoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class UserContext {
    private Long userId;
    private UserRoleEnum role;
    private String username;
    private User user;
}
