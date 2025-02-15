package com.p1.nomnom.ai.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.p1.nomnom.ai.dto.request.CreateDescriptionRequest;
import com.p1.nomnom.ai.dto.response.GeneratedDescriptionResponse;
import com.p1.nomnom.ai.entity.Ai;
import com.p1.nomnom.ai.repository.AiRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

@Service
public class AiService {

    private final AiRepository aiRepository;

    public AiService(AiRepository aiRepository) {
        this.aiRepository = aiRepository;
    }

    public GeneratedDescriptionResponse generateDescription(CreateDescriptionRequest request) {
        try {
            // 1. Google Credentials 로드
            String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath))
                    .createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));
            credentials.refreshIfExpired();
            String accessToken = credentials.getAccessToken().getTokenValue();

            // 2. HTTP 요청 준비
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 3. 요청 Body 생성
            String prompt = String.format(
                    "음식 이름: %s. 질문: %s. 힌트: %s",
                    request.getFoodName(),
                    request.getQuestion(),
                    request.getDescriptionHint() != null ? request.getDescriptionHint() : "없음"
            );

            String requestBody = String.format(
                    "{\"prompt\": \"%s\", \"parameters\": {\"temperature\": 0.7, \"maxOutputTokens\": 256}}",
                    prompt
            );

            HttpEntity<String> httpRequest = new HttpEntity<>(requestBody, headers);

            // 4. API 호출
            String apiEndpoint = "https://gemini.googleapis.com/v1/projects/YOUR_PROJECT_ID/locations/YOUR_LOCATION/endpoints/YOUR_ENDPOINT_ID:predict";
            ResponseEntity<String> response = restTemplate.exchange(
                    apiEndpoint, HttpMethod.POST, httpRequest, String.class
            );

            // 5. 응답 처리
            String aiAnswer = parseAiResponse(response.getBody()); // 응답 데이터 파싱
            if (aiAnswer == null || aiAnswer.isEmpty()) {
                throw new RuntimeException("AI 응답이 비어 있습니다.");
            }

            // 6. 데이터 저장
            Ai ai = new Ai();
            ai.setQuestion(request.getQuestion());
            ai.setAnswer(aiAnswer); // AI가 생성한 설명
            ai.setFoodName(request.getFoodName());
            ai.setStoreId(request.getStoreId());
            ai.setDescriptionHint(request.getDescriptionHint());
            ai.setKeyword(request.getKeyword());

            aiRepository.save(ai);

            // 7. 응답 반환
            return new GeneratedDescriptionResponse(ai.getId(), aiAnswer);

        } catch (IOException e) {
            throw new RuntimeException("Google Credentials 로드 실패: " + e.getMessage(), e);
        }
    }

    // AI 응답 파싱 메서드
    private String parseAiResponse(String responseBody) {
        // 간단한 JSON 파싱 로직 (추후 필요하면 JSON 라이브러리 사용 가능)
        if (responseBody.contains("response")) {
            return responseBody.split("response\":\"")[1].split("\"")[0];
        }
        return null;
    }
}
