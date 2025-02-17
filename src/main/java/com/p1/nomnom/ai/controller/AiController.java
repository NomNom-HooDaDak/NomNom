package com.p1.nomnom.ai.controller;

import com.p1.nomnom.ai.dto.request.AiRequestDto;
import com.p1.nomnom.ai.dto.response.AiResponseDto;
import com.p1.nomnom.ai.entity.Ai;
import com.p1.nomnom.ai.service.AiService;
import com.p1.nomnom.store.service.StoreService;
import com.p1.nomnom.ai.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/nom/ai")
public class AiController {

    private final AiService aiService;
    private final GeminiService geminiService;
    private final StoreService storeService;  // StoreService를 추가합니다.

    @Autowired
    public AiController(AiService aiService, GeminiService geminiService, StoreService storeService) {
        this.aiService = aiService;
        this.geminiService = geminiService;
        this.storeService = storeService;  // StoreService 주입
    }

    // AI 상품 설명 자동 생성
    @PostMapping("/foods/description")
    public AiResponseDto generateFoodDescription(@RequestBody AiRequestDto requestDto) {
        try {
            // Gemini로 텍스트 생성 요청 (question, descriptionHint, keyword를 포함)
            String generatedDescription = geminiService.generateContent(
                    requestDto.getQuestion(),
                    requestDto.getDescriptionHint(),
                    requestDto.getKeyword()
            );

            // storeId로 storeName을 찾기
            String storeName = storeService.getStoreNameById(requestDto.getStoreId());

            // AI 응답을 DB에 저장
            Ai aiEntity = new Ai();
            aiEntity.setQuestion(requestDto.getQuestion());  // 받아온 질문을 저장
            aiEntity.setAnswer(generatedDescription);
            aiEntity.setFoodName(requestDto.getFoodName());
            aiEntity.setDescriptionHint(requestDto.getDescriptionHint());
            aiEntity.setKeyword(requestDto.getKeyword());
            aiEntity.setStoreId(requestDto.getStoreId());  // store_id는 UUID로 변환

            aiService.save(aiEntity);  // DB에 저장 aiService - save 메서드

            // 응답 객체 반환
            return new AiResponseDto(
                    aiEntity.getQuestion(),
                    aiEntity.getFoodName(),
                    aiEntity.getStoreId(),
                    storeName,
                    aiEntity.getDescriptionHint(),
                    aiEntity.getKeyword(),
                    generatedDescription
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new AiResponseDto("Error generating description", "", null, "", "", "", "Error generating description");
        }
    }
}
