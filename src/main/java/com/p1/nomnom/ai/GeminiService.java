package com.p1.nomnom.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.io.IOException;

@Service
public class GeminiService {

    @Value("${google.api.key}")
    private String apiKey;  // API 키를 application.properties에서 주입받음

    // question과 descriptionHint, keyword를 바탕으로 설명을 생성하는 메서드
    public String generateContent(String question, String descriptionHint, String keyword) {
        // 요청 텍스트 생성
        String fullRequestText = "질문: " + question + " " + descriptionHint + " " + keyword + "에 대해 설명해주세요.";

        // Gemini로 요청 보내기
        return sendRequestToGemini(fullRequestText);
    }

    // Gemini API로 요청을 보내는 메서드
    private String sendRequestToGemini(String prompt) {
        // REST API 호출을 위한 설정
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + apiKey;
        RestTemplate restTemplate = new RestTemplate();

        // 요청 데이터 준비
        String requestBody = "{\"contents\": [{\"parts\": [{\"text\": \"" + prompt + "\"}]}]}";

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HTTP 요청 설정
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // API 호출 및 응답 처리
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // 응답 본문 반환
        return response.getBody();
    }
}
