package com.p1.nomnom.security.aop;

import com.p1.nomnom.user.entity.UserRoleEnum;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleCheck {
    UserRoleEnum[] value(); // 특정 역할만 허용 (배열)
}
