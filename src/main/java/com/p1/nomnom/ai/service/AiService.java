package com.p1.nomnom.ai.service;

import com.p1.nomnom.ai.dto.request.AiRequestDto;
import com.p1.nomnom.ai.dto.response.AiResponseDto;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

public interface AiService {
    AiResponseDto getAiAnswer(AiRequestDto requestDto);

    //AiResponseDto generateFoodDescription(AiRequestDto requestDto); //

    List<AiResponseDto> getAllAiAnswers(int page, int size, Sort sort);

    List<AiResponseDto> getAiAnswersByStore(UUID storeId, int page, int size, Sort sort);

    List<AiResponseDto> searchAiAnswersByKeyword(String keyword, int page, int size, Sort sort);

    AiResponseDto hideAiAnswer(UUID aiId, String deletedBy);
    AiResponseDto restoreAiAnswer(UUID aiId, String updatedBy);



}
