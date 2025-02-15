package com.p1.nomnom.ai.service;

import com.p1.nomnom.ai.dto.request.CreateDescriptionRequest;
import com.p1.nomnom.ai.dto.response.GeneratedDescriptionResponse;
import com.p1.nomnom.ai.entity.Ai;
import com.p1.nomnom.ai.repository.AiRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

public class AiServiceTest {

    @Test
    public void testGenerateDescription() {
        // Mock AiRepository
        AiRepository aiRepository = Mockito.mock(AiRepository.class);
        AiService aiService = new AiService(aiRepository);

        // Create test request
        CreateDescriptionRequest request = new CreateDescriptionRequest();
        request.setFoodName("Pasta");
        request.setStoreId(null); // Optional: UUID 사용 가능
        request.setDescriptionHint("Delicious Italian cuisine");
        request.setKeyword("Italian");

        // Mocking API Response
        GeneratedDescriptionResponse response = aiService.generateDescription(request);

        // Assert
        assertThat(response).isNotNull();
        System.out.println("Generated Description: " + response.getGeneratedDescription());
    }
}
