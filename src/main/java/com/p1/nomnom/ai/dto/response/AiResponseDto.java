package com.p1.nomnom.ai.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AiResponseDto {
    private String question;            // AI가 받은 질문
    private String foodName;            // 음식 이름
    private UUID storeId;               // storeId (UUID 형식)
    private String storeName;           // storeName - 입력받은 storeId로 이름 찾아오기
    private String descriptionHint;     // 설명 힌트
    private String keyword;             // 설명 키워드
    private String generatedDescription; // AI가 생성한 설명

    // 기본 생성자 추가
    public AiResponseDto() {
    }

    // 생성자 추가
    public AiResponseDto(String question, String foodName, UUID storeId, String storeName,
                         String descriptionHint, String keyword, String generatedDescription) {
        this.question = question;
        this.foodName = foodName;
        this.storeId = storeId;
        this.storeName = storeName;
        this.descriptionHint = descriptionHint;
        this.keyword = keyword;
        this.generatedDescription = generatedDescription;
    }
}
