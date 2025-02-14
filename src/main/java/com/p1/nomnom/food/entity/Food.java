package com.p1.nomnom.food.entity;

import com.p1.nomnom.food.dto.request.FoodRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name="p_food") // 1. 테이블명 F를 대문자로 해야하는지?
@Getter
@Setter
public class Food { // 기본 엔터티 만들어서 extends 할 예정

    // UUID로 바꾸어야함.
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // 프로젝트 설명을 보면 UUID 로 지정되어 있어 UUID 로 설정
    private UUID id;

    /*
    * 2. 음식 엔터티를 기준으로
    * 여러 개의 음식 정보를 하나의 가게가 갖을 수 있다 --> 이렇게 접근해야 하는 건가요?
    *
    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    */
    // private Store store; // Store 고유 id, Store Entity의 pk를 참조하는 외래 키
    private String storeId;

    @Column(nullable = false) // 음식명 컬럼에 null을 허용하지 않음
    private String name; // 음식명

    private String description; // 음식 설명, 이 컬럼은 null 이어도 된다.

    @Column(nullable = false)
    // 음식 가격 값의 타입을 숫자 타입으로 바꾸어야 하나?
    // 문자열로 입력받으면 안되는 건지, 그리고 만약 숫자타입으로 할거면 BigDecimal, int, Long 타입 고려하라고 피드백 받음
    private String price; // 음식 가격

    // 음식 사진은 null 이어도 된다.

    // 일단 주소로 받기
    // --- jpg나 png로 들어온 것을 로컬에 저장하든 클라우드에 저장하든 해야함.
    private String image; // 음식 사진 url

    public Food(String storeId, FoodRequestDto requestDto) {
        // id UUID 타입? 자동설정이니 따로 대입하지 않아도 객체 생성시 알아서 필드가 설정되는 건지?
        // Store 타입 store 참조변수에는 Store 객체를 만들어서 대입해야 하는지?
        this.storeId = storeId;
        this.name = requestDto.getName();
        this.description = requestDto.getDescription();
        this.price = requestDto.getPrice();
        this.image = requestDto.getImage();
    }
}
