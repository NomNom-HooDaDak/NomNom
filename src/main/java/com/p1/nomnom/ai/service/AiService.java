package com.p1.nomnom.ai.service;

import com.p1.nomnom.ai.dto.request.AiRequestDto;
import com.p1.nomnom.ai.dto.response.AiResponseDto;
import com.p1.nomnom.ai.entity.Ai;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

public interface AiService {
    AiResponseDto getAiAnswer(AiRequestDto requestDto);

    void save(Ai aiEntity);

    List<AiResponseDto> searchAi(UUID storeId, String question, int pageSize, Sort sort);
}
