package com.p1.nomnom.payment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity
@Table(name="p_payment")
@Getter
@Setter
@NoArgsConstructor
public class Payment { // 기본 엔터티 만들어서 extends 할 예정

    // 3. @ToString 애너테이션 작성해야 String 값으로 DB에 전달하기 위해
    @ToString
    private enum Method {
        CARD, CHECK
    }

    //    Method 가 현금인 경우 아예 DONE 처리
    //    Method 가 카드인 경우 FAIL, SUCCESS 로 처리
    @ToString // db 문자열에 맞게 보내기???
    private enum Status {
        PROGRESS,
        FAIL,
        SUCCESS
    }

    // 주문테이블에 주문데이터가 먼저들어오고
    // 주문데이터를 가지고 결제를 진행할 겁니다.

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // auto_increment 설정
    private UUID id;


    // method =card > toss api > status = done, fail =>  status = fail
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Method method;

    @Column(nullable = false) // 음식명 컬럼에 null을 허용하지 않음
    @Enumerated(EnumType.STRING)
    private Status status;

    // private Order order; // order 테이블의 pk를 참조함
    // order 테이블에 total_price 가 있어서 @OneToOne
    private String orderId;

    // 결제 식별 역할, 고유한, 빈 값 허용하지 않음
    @Column(unique = true, nullable = false)
    private String paymentKey;
}
