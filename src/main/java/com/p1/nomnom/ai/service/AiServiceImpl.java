package com.p1.nomnom.ai.service;

import com.p1.nomnom.ai.GeminiService;
import com.p1.nomnom.ai.dto.request.AiRequestDto;
import com.p1.nomnom.ai.dto.response.AiResponseDto;
import com.p1.nomnom.ai.entity.Ai;
import com.p1.nomnom.ai.repository.AiRepository;
import com.p1.nomnom.ai.utils.AiResponseParser;
import com.p1.nomnom.security.aop.UserContext;
import com.p1.nomnom.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiServiceImpl implements AiService {

    private final AiRepository aiRepository;
    private final StoreService storeService;
    private final GeminiService geminiService;

    @Transactional
    @Override
    public AiResponseDto getAiAnswer(AiRequestDto requestDto, UserContext userContext) {
        try {
            String modifiedQuestion = requestDto.getQuestion().trim() + " 답변을 최대한 간결하게 50자 이하로";
            String generatedDescription = geminiService.generateContent(
                    modifiedQuestion,
                    requestDto.getDescriptionHint(),
                    requestDto.getKeyword()
            );
            String extractedText = AiResponseParser.extractTextFromGeneratedDescription(generatedDescription);

            if (extractedText.length() > 255) {
                extractedText = extractedText.substring(0, 255);
            }

            Ai ai = Ai.builder()
                    .question(requestDto.getQuestion())
                    .foodName(requestDto.getFoodName())
                    .storeId(requestDto.getStoreId())
                    .descriptionHint(requestDto.getDescriptionHint())
                    .keyword(requestDto.getKeyword())
                    .answer(extractedText)
                    .generatedDescription(generatedDescription) // 원본 JSON 저장 추가
                    .hidden(Boolean.FALSE) // 기본값 false 설정 추가
                    .build();
            aiRepository.save(ai);

            String storeName = storeService.getStoreNameById(requestDto.getStoreId());

            return AiResponseDto.builder()
                    .question(ai.getQuestion())
                    .foodName(ai.getFoodName())
                    .storeId(ai.getStoreId())
                    .storeName(storeName)
                    .descriptionHint(ai.getDescriptionHint())
                    .keyword(ai.getKeyword())
                    .generatedDescription(ai.getGeneratedDescription()) // 원본 JSON 반환 추가
                    .answer(ai.getAnswer())
                    .hidden(ai.getHidden() != null ? ai.getHidden() : Boolean.FALSE) // null 방지
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("음식 설명 생성 중 오류 발생", e);
        }
    }

    @Override
    public List<AiResponseDto> getAllAiAnswers(int page, int size, Sort sort) {
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Ai> pageResult = aiRepository.findAll(pageable);

        return pageResult.stream()
                .map(ai -> AiResponseDto.builder()
                        .question(ai.getQuestion())
                        .foodName(ai.getFoodName())
                        .storeId(ai.getStoreId())
                        .storeName(storeService.getStoreNameById(ai.getStoreId()))
                        .descriptionHint(ai.getDescriptionHint())
                        .keyword(ai.getKeyword())
                        .generatedDescription(ai.getGeneratedDescription()) // 원본 JSON 추가
                        .answer(ai.getAnswer())
                        .hidden(ai.getHidden() != null ? ai.getHidden() : Boolean.FALSE) // null 방지
                        .build())
                .toList();
    }

    @Override
    public List<AiResponseDto> getAiAnswersByStore(UUID storeId, int page, int size, Sort sort) {
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Ai> pageResult = aiRepository.findAllByStoreId(storeId, pageable);

        return pageResult.stream()
                .map(ai -> AiResponseDto.builder()
                        .question(ai.getQuestion())
                        .foodName(ai.getFoodName())
                        .storeId(ai.getStoreId())
                        .storeName(storeService.getStoreNameById(ai.getStoreId()))
                        .descriptionHint(ai.getDescriptionHint())
                        .keyword(ai.getKeyword())
                        .generatedDescription(ai.getGeneratedDescription()) // 원본 JSON 추가
                        .answer(ai.getAnswer())
                        .hidden(ai.getHidden() != null ? ai.getHidden() : Boolean.FALSE) // null 방지
                        .build())
                .toList();
    }

    @Override
    public List<AiResponseDto> searchAiAnswersByKeyword(String keyword, int page, int size, Sort sort) {
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Ai> pageResult = aiRepository.findByKeywordContaining(keyword, pageable);

        return pageResult.stream()
                .map(ai -> AiResponseDto.builder()
                        .question(ai.getQuestion())
                        .foodName(ai.getFoodName())
                        .storeId(ai.getStoreId())
                        .storeName(storeService.getStoreNameById(ai.getStoreId()))
                        .descriptionHint(ai.getDescriptionHint())
                        .keyword(ai.getKeyword())
                        .generatedDescription(ai.getGeneratedDescription()) // 원본 JSON 추가
                        .answer(ai.getAnswer())
                        .hidden(ai.getHidden() != null ? ai.getHidden() : Boolean.FALSE) // null 방지
                        .build())
                .toList();
    }

    @Transactional
    @Override
    public AiResponseDto hideAiAnswer(UUID aiId, UserContext userContext) {
        Ai ai = aiRepository.findById(aiId)
                .orElseThrow(() -> new IllegalArgumentException("해당 AI 응답을 찾을 수 없습니다."));
        ai.hide(userContext.getUsername());
        aiRepository.save(ai);
        return AiResponseDto.builder()
                .question(ai.getQuestion())
                .foodName(ai.getFoodName())
                .storeId(ai.getStoreId())
                .storeName(storeService.getStoreNameById(ai.getStoreId()))
                .descriptionHint(ai.getDescriptionHint())
                .keyword(ai.getKeyword())
                .generatedDescription(ai.getGeneratedDescription()) // 원본 JSON 추가
                .answer(ai.getAnswer())
                .hidden(ai.getHidden() != null ? ai.getHidden() : Boolean.FALSE) // null 방지
                .build();
    }

    @Transactional
    @Override
    public AiResponseDto restoreAiAnswer(UUID aiId,UserContext userContext) {
        Ai ai = aiRepository.findById(aiId)
                .orElseThrow(() -> new IllegalArgumentException("해당 AI 응답을 찾을 수 없습니다."));
        ai.restore(userContext.getUsername());
        aiRepository.save(ai);
        return AiResponseDto.builder()
                .question(ai.getQuestion())
                .foodName(ai.getFoodName())
                .storeId(ai.getStoreId())
                .storeName(storeService.getStoreNameById(ai.getStoreId()))
                .descriptionHint(ai.getDescriptionHint())
                .keyword(ai.getKeyword())
                .generatedDescription(ai.getGeneratedDescription()) // 원본 JSON 추가
                .answer(ai.getAnswer())
                .hidden(ai.getHidden() != null ? ai.getHidden() : Boolean.FALSE) // null 방지
                .build();
    }

    // 특정 가게가 foodName 기반으로 ai 서비스를 이용한 답변 여부 확인,
    // ai 서비스를 이용한 답변이 존재한다면 가장 최신의 답변만 반환한다.
    @Override
    public String findFirstAnswerByStoreAndFoodName(UUID storeId, String foodName) {
        Ai firstAnswerByStoreAndFoodName = aiRepository.findFirstAnswerByStoreAndFoodName(storeId, foodName);
        // log.info("ai 서비스 이용 안했을 때 어떤 데이터가 반환될까: {}", firstAnswerByStoreAndFoodName); null 이 반환됨.

        if(firstAnswerByStoreAndFoodName != null) {
            log.info("AnswerByStoreAndFoodName: {}, {}", firstAnswerByStoreAndFoodName.getAnswer(), firstAnswerByStoreAndFoodName.getFoodName());
            return firstAnswerByStoreAndFoodName.getAnswer();
        }   return 1+"";
    }
}
