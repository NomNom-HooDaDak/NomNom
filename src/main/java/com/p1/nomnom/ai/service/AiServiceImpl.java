package com.p1.nomnom.ai.service;

import com.p1.nomnom.ai.GeminiService;
import com.p1.nomnom.ai.dto.request.AiRequestDto;
import com.p1.nomnom.ai.dto.response.AiResponseDto;
import com.p1.nomnom.ai.entity.Ai;
import com.p1.nomnom.ai.repository.AiRepository;
import com.p1.nomnom.store.service.StoreService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final AiRepository aiRepository;
    private final StoreService storeService;
    private final GeminiService geminiService;  // 🚀 Gemini AI 연동 추가

    @Transactional
    @Override
    public AiResponseDto getAiAnswer(AiRequestDto requestDto) {
        // 요청 텍스트 마지막에 문구 추가 (사용량 절감 목적)
        String modifiedQuestion = requestDto.getQuestion().trim() + " 답변을 최대한 간결하게 50자 이하로";

        // Gemini AI API 호출
        String answer = geminiService.generateContent(
                modifiedQuestion,
                requestDto.getDescriptionHint(),
                requestDto.getKeyword()
        );

        //255자 이상이면 자르기
        if (answer.length() > 255) {
            answer = answer.substring(0, 255);
        }

        // AI 응답 저장
        Ai aiEntity = new Ai();
        aiEntity.setQuestion(requestDto.getQuestion());
        aiEntity.setAnswer(answer);
        aiEntity.setFoodName(requestDto.getFoodName());
        aiEntity.setDescriptionHint(requestDto.getDescriptionHint());
        aiEntity.setKeyword(requestDto.getKeyword());
        aiEntity.setStoreId(requestDto.getStoreId());

        aiRepository.save(aiEntity);

        // storeId로 storeName을 조회
        String storeName = storeService.getStoreNameById(requestDto.getStoreId());

        return new AiResponseDto(
                aiEntity.getQuestion(),
                aiEntity.getFoodName(),
                aiEntity.getStoreId(),
                storeName,
                aiEntity.getDescriptionHint(),
                aiEntity.getKeyword(),
                answer,
                aiEntity.getHidden()
        );
    }

    @Override
    public List<AiResponseDto> getAllAiAnswers(int page, int size, Sort sort) {
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Ai> pageResult = aiRepository.findAll(pageable);

        return pageResult.stream()
                .map(ai -> new AiResponseDto(ai.getQuestion(), ai.getFoodName(), ai.getStoreId(),
                        storeService.getStoreNameById(ai.getStoreId()), ai.getDescriptionHint(), ai.getKeyword(),
                        ai.getAnswer()))
                .toList();
    }

    @Override
    public List<AiResponseDto> getAiAnswersByStore(UUID storeId, int page, int size, Sort sort) {
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Ai> pageResult = aiRepository.findAllByStoreId(storeId, pageable);

        return pageResult.stream()
                .map(ai -> new AiResponseDto(ai.getQuestion(), ai.getFoodName(), ai.getStoreId(),
                        storeService.getStoreNameById(ai.getStoreId()), ai.getDescriptionHint(), ai.getKeyword(),
                        ai.getAnswer()))
                .toList();
    }

    @Override
    public List<AiResponseDto> searchAiAnswersByKeyword(String keyword, int page, int size, Sort sort) {
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Ai> pageResult = aiRepository.findByKeywordContaining(keyword, pageable);

        return pageResult.stream()
                .map(ai -> new AiResponseDto(ai.getQuestion(), ai.getFoodName(), ai.getStoreId(),
                        storeService.getStoreNameById(ai.getStoreId()), ai.getDescriptionHint(), ai.getKeyword(),
                        ai.getAnswer()))
                .toList();
    }

    @Transactional
    @Override
    public AiResponseDto hideAiAnswer(UUID aiId, String deletedBy) {
        Ai ai = aiRepository.findById(aiId)
                .orElseThrow(() -> new IllegalArgumentException("해당 AI 응답을 찾을 수 없습니다."));

        ai.hide(deletedBy);
        aiRepository.save(ai);

        // 숨김 처리된 상태를 포함한 응답 반환
        return new AiResponseDto(
                ai.getQuestion(),
                ai.getFoodName(),
                ai.getStoreId(),
                storeService.getStoreNameById(ai.getStoreId()),
                ai.getDescriptionHint(),
                ai.getKeyword(),
                ai.getAnswer(),
                ai.getHidden()  // hidden 값 포함
        );
    }

    @Transactional
    @Override
    public AiResponseDto restoreAiAnswer(UUID aiId, String updatedBy) {
        Ai ai = aiRepository.findById(aiId)
                .orElseThrow(() -> new IllegalArgumentException("해당 AI 응답을 찾을 수 없습니다."));

        ai.restore(updatedBy); // 복구 메서드 호출
        aiRepository.save(ai);

        // 복구된 상태를 포함한 응답 반환
        return new AiResponseDto(
                ai.getQuestion(),
                ai.getFoodName(),
                ai.getStoreId(),
                storeService.getStoreNameById(ai.getStoreId()),
                ai.getDescriptionHint(),
                ai.getKeyword(),
                ai.getAnswer(),
                ai.getHidden() // hidden 값 포함
        );
    }
}
