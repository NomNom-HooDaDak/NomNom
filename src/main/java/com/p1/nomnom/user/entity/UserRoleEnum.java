package com.p1.nomnom.user.entity;

public enum UserRoleEnum {
    CUSTOMER(Authority.CUSTOMER),  // 일반 고객
    OWNER(Authority.OWNER),        // 가게 사장
    MANAGER(Authority.MANAGER),    // 관리자
    MASTER(Authority.MASTER);      // 최상위 관리자

    private final String authority;

    UserRoleEnum(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public static class Authority {
        public static final String CUSTOMER = "ROLE_CUSTOMER";
        public static final String OWNER = "ROLE_OWNER";
        public static final String MANAGER = "ROLE_MANAGER";
        public static final String MASTER = "ROLE_MASTER";
    }

}

