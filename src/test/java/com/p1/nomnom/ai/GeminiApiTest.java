package com.p1.nomnom.ai;

import com.p1.nomnom.ai.dto.request.CreateDescriptionRequest;
import com.p1.nomnom.ai.service.AiService;
import com.p1.nomnom.ai.repository.AiRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class GeminiApiTest {

    @Test
    public void testGenerateDescription() {
        // Mock AiRepository
        AiRepository aiRepository = Mockito.mock(AiRepository.class);

        // Inject mock into AiService
        AiService aiService = new AiService(aiRepository);

        // Create a sample request
        CreateDescriptionRequest request = new CreateDescriptionRequest();
        request.setFoodName("Pasta");
        request.setStoreId(null); // Replace with a valid UUID if required
        request.setDescriptionHint("Delicious Italian cuisine");
        request.setKeyword("Italian");

        // Call the service method
        String response = aiService.generateDescription(request).getGeneratedDescription();

        // Print the response (for debugging)
        System.out.println("Generated Description: " + response);
    }
}
