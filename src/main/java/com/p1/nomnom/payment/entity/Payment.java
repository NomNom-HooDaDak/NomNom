package com.p1.nomnom.payment.entity;

import com.p1.nomnom.common.entity.BaseEntity;
import com.p1.nomnom.orders.entity.Order;
import com.p1.nomnom.store.entity.Store;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="p_payment")
@Getter
@Setter
@NoArgsConstructor
public class Payment extends BaseEntity {
    @ToString
    public enum Method {
        CARD, CHECK
    }

    //    Method 가 현금인 경우 아예 DONE 처리
    //    Method 가 카드인 경우 FAIL, SUCCESS 로 처리
    @ToString
    public enum CurrentStatus{
        PROGRESS,
        FAIL,
        SUCCESS
    }
    // 주문테이블에 주문데이터가 먼저들어오고
    // 주문데이터를 가지고 결제를 진행할 겁니다.
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // auto_increment 설정
    private UUID id;

    @Column(name="user_id", nullable = false)
    private Long userId;

    // method =card > toss api > status = done, fail =>  status = fail
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Method method;

    @Column(name="status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CurrentStatus currentStatus;

    // 결제 식별 역할, 고유한, 빈 값 허용하지 않음
    @Column(unique = true, nullable = false)
    private UUID paymentKey;

    @ManyToOne
    @JoinColumn(name = "store_id")  // DB에서는 store_id로 매핑
    private Store store;

    @OneToOne
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    public void createPaymentKey() {
        this.paymentKey = UUID.randomUUID();
    }

    public void createdBy(String username) {
        this.createdAt = LocalDateTime.now();
        this.createdBy = username;
    }
}
