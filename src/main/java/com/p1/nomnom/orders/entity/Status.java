package com.p1.nomnom.orders.entity;

public enum Status {
    PENDING,    // 주문 접수 대기
    CONFIRMED,  // 주문 확인됨
    SHIPPED,    // 배송 중
    DELIVERED,  // 배송 완료
    CANCELED    // 주문 취소
}