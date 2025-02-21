package com.p1.nomnom.food.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class FoodRequestDto {
    // private Store store;
    /* description(음식설명 필드)과 image 값은 필수가 아님 */
    private String name;
    @Setter
    private String description;
    private Long price;
    // 일단 문자열 타입으로 받고,
    // 확장자가 .jpg .png 등 사진파일로 들어온 것을 로컬에 저장하든 클라우드에 저장하든 하는 로직을 추가하기
    private String image;
}
