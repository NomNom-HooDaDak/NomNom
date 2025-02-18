package com.p1.nomnom.food.entity;

import com.p1.nomnom.common.entity.BaseEntity;
import com.p1.nomnom.food.dto.request.FoodRequestDto;
import com.p1.nomnom.store.entity.Store;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="p_food")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Food extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store; // Store 고유 id, Store Entity의 pk를 참조하는 외래 키
    // private String storeId;

    @Column(nullable = false) // 음식명 컬럼에 null을 허용하지 않음
    private String name; // 음식명

    private String description; // 음식 설명, 이 컬럼은 null 이어도 된다.

    @Column(nullable = false)
    // 음식 가격 값의 타입이 숫자 타입의 경우
    // BigDecimal, int, Long 타입 고려해야함
    private String price; // 음식 가격

    // 음식 사진은 null 이어도 된다.

    // 일단 주소로 받기
    // --- jpg나 png로 들어온 것을 로컬에 저장하든 클라우드에 저장하든 해야함.
    private String image; // 음식 사진 url

    @Column(nullable = false)
    private Boolean hidden = false;

    public Food(Store store, FoodRequestDto requestDto) {
        this.store = store;
        this.name = requestDto.getName();
        this.description = requestDto.getDescription();
        this.price = requestDto.getPrice();
        this.image = requestDto.getImage();
    }

    // 음식 메뉴 숨김 처리 ( 삭제 처리 )
    public void hide(String deletedBy) {
        hidden = true;
        this.deletedAt = LocalDateTime.now();
        this.markAsDeleted(deletedBy);
    }

    // 업데이트한 사람과 일자 업데이트 하는 메서드
    public void updateBy(String updateBy) {
        this.setUpdatedAt(LocalDateTime.now());
        this.setUpdatedBy(updateBy);
    }

    // 생성할 때 ( 처음 값 그대로 감)
    public void createBy(String createBy) {
        this.setCreatedBy(createBy);
        this.setCreatedAt(LocalDateTime.now());
    }
}