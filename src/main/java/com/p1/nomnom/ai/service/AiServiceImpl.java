package com.p1.nomnom.ai.service;

import com.p1.nomnom.ai.dto.request.AiRequestDto;
import com.p1.nomnom.ai.dto.response.AiResponseDto;
import com.p1.nomnom.ai.entity.Ai;
import com.p1.nomnom.ai.repository.AiRepository;
import com.p1.nomnom.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
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

    @Transactional
    @Override
    public AiResponseDto getAiAnswer(AiRequestDto requestDto) {
        String answer = callAiApi(requestDto.getQuestion()); // AI API 호출 (예제)

        Ai aiEntity = new Ai();
        aiEntity.setQuestion(requestDto.getQuestion());
        aiEntity.setAnswer(answer);
        aiEntity.setFoodName(requestDto.getFoodName());
        aiEntity.setDescriptionHint(requestDto.getDescriptionHint());
        aiEntity.setKeyword(requestDto.getKeyword());
        aiEntity.setStoreId(requestDto.getStoreId());

        aiRepository.save(aiEntity);

        // storeId로 storeName을 조회
        UUID storeId = requestDto.getStoreId();
        String storeName = storeService.getStoreNameById(storeId);

        return new AiResponseDto(
                aiEntity.getQuestion(),
                aiEntity.getFoodName(),
                storeId,
                storeName,
                aiEntity.getDescriptionHint(),
                aiEntity.getKeyword(),
                answer
        );
    }

    @Override
    public void save(Ai aiEntity) {
        aiRepository.save(aiEntity);
    }

    @Override
    @Transactional
    public List<AiResponseDto> searchAi(UUID storeId, String question, int pageSize, Sort sort) {
        Pageable pageable = PageRequest.of(0, pageSize, sort);
        Page<Ai> pageResult;

        if (question != null && !question.isEmpty()) {
            pageResult = aiRepository.findByQuestionContainingAndStoreId(question, storeId, pageable);
        } else {
            pageResult = aiRepository.findAllByStoreId(storeId, pageable);
        }

        return pageResult.stream()
                .map(ai -> new AiResponseDto(ai.getQuestion(), ai.getFoodName(), ai.getStoreId(),
                        storeService.getStoreNameById(ai.getStoreId()), ai.getDescriptionHint(), ai.getKeyword(),
                        ai.getAnswer()))
                .toList();
    }

    // AI API 호출 예제 (실제 API 연동 필요)
    private String callAiApi(String inputText) {
        return "예제 AI 응답"; // AI API의 실제 응답으로 변경 필요
    }
}
