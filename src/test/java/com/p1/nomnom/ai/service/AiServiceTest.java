package com.p1.nomnom.ai.service;

import com.p1.nomnom.ai.dto.request.AiRequestDto;
import com.p1.nomnom.ai.dto.response.AiResponseDto;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AiServiceTest {

    AiService aiService = new AiService();

    @Test
    void testGetAiAnswer() {
        AiRequestDto requestDto = new AiRequestDto();
        requestDto.setQuestion("What is pizza?");
        requestDto.setFoodName("Pizza");
        requestDto.setDescriptionHint("Italian dish");
        requestDto.setKeyword("food");

        AiResponseDto responseDto = aiService.getAiAnswer(requestDto);

        assertNotNull(responseDto);
        //assertEquals("AI로부터 받은 답변", responseDto.getAnswer());
        assertEquals("Pizza", responseDto.getFoodName());
    }
}
