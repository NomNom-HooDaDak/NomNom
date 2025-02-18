package com.p1.nomnom.food.entity;

//import com.p1.nomnom.food.dto.request.FoodRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name="p_food")
@Getter
@Setter
@NoArgsConstructor
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    /*
    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    */
    // private Store store; // Store 고유 id, Store Entity의 pk를 참조하는 외래 키
    private String storeId;

    @Column(nullable = false) // 음식명 컬럼에 null을 허용하지 않음
    private String name; // 음식명

    private String description; // 음식 설명, 이 컬럼은 null 이어도 된다.

    @Column(nullable = false)
    // 음식 가격 값의 타입을 숫자 타입으로 바꾼다면
    // BigDecimal, int, Long 타입 고려해야함
    private String price; // 음식 가격

    // 음식 사진은 null 이어도 된다.

    // 일단 주소로 받기
    // --- jpg나 png로 들어온 것을 로컬에 저장하든 클라우드에 저장하든 해야함.
    private String image; // 음식 사진 url

//    public Food(String storeId, FoodRequestDto requestDto) {
//        this.storeId = storeId;
//        this.name = requestDto.getName();
//        this.description = requestDto.getDescription();
//        this.price = requestDto.getPrice();
//        this.image = requestDto.getImage();
//    }
}