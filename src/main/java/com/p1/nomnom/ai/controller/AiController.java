package com.p1.nomnom.ai.controller;

import com.p1.nomnom.ai.dto.request.AiRequestDto;
import com.p1.nomnom.ai.dto.response.AiResponseDto;
import com.p1.nomnom.ai.entity.Ai;
import com.p1.nomnom.ai.service.AiService;
import com.p1.nomnom.ai.GeminiService;
import com.p1.nomnom.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nom/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;
    private final GeminiService geminiService;
    private final StoreService storeService;

    // AI 상품 설명 자동 생성
    @PostMapping("/foods/description")
    public AiResponseDto generateFoodDescription(@RequestBody AiRequestDto requestDto) {
        try {
            // 🚀 요청 텍스트 마지막에 문구 추가
            String modifiedQuestion = requestDto.getQuestion().trim() + " 답변을 최대한 간결하게 50자 이하로";

            // AI API 호출
            String generatedDescription = geminiService.generateContent(
                    modifiedQuestion,
                    requestDto.getDescriptionHint(),
                    requestDto.getKeyword()
            );

            // 🔥 255자 이상이면 자르기
            if (generatedDescription.length() > 255) {
                generatedDescription = generatedDescription.substring(0, 255);
            }

            // storeId로 storeName 조회
            String storeName = storeService.getStoreNameById(requestDto.getStoreId());

            // AI 응답을 DB에 저장
            Ai aiEntity = new Ai();
            aiEntity.setQuestion(requestDto.getQuestion());
            aiEntity.setAnswer(generatedDescription);
            aiEntity.setFoodName(requestDto.getFoodName());
            aiEntity.setDescriptionHint(requestDto.getDescriptionHint());
            aiEntity.setKeyword(requestDto.getKeyword());
            aiEntity.setStoreId(requestDto.getStoreId());

            aiService.save(aiEntity); // save 메서드 사용

            // 응답 반환
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

    // 기존 기능 유지: AI 질문 및 응답 가져오기
    @PostMapping("/answer")
    public AiResponseDto getAiAnswer(@RequestBody AiRequestDto requestDto) {
        return aiService.getAiAnswer(requestDto);
    }




}
