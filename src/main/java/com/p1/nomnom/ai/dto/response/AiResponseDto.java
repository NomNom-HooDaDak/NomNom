package com.p1.nomnom.ai.dto.response;

//import lombok.AllArgsConstructor;
import lombok.*;
//import lombok.NoArgsConstructor;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@AllArgsConstructor
public class AiResponseDto {
    private String question;            // AI가 받은 질문
    private String foodName;            // 음식 이름
    private UUID storeId;               // storeId (UUID 형식)
    private String storeName;           // storeName - 입력받은 storeId로 이름 찾아오기
    private String descriptionHint;     // 설명 힌트
    private String keyword;             // 설명 키워드
    private String generatedDescription; // AI가 생성한 설명(원본 JSON 형식)
    private boolean hidden; //숨김 상태
    private String answer; // 파싱된 텍스트만 저장

    // 생성자 - Builder했음
//    public AiResponseDto(String question, String foodName, UUID storeId, String storeName,
//                         String descriptionHint, String keyword, String generatedDescription) {
//        this.question = question;
//        this.foodName = foodName;
//        this.storeId = storeId;
//        this.storeName = storeName;
//        this.descriptionHint = descriptionHint;
//        this.keyword = keyword;
//        this.generatedDescription = generatedDescription;
//        this.hidden = false;
//    }
}
