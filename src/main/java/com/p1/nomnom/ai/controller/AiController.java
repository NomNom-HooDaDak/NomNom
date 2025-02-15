package com.p1.nomnom.ai.controller;

import com.p1.nomnom.ai.dto.request.CreateDescriptionRequest;
import com.p1.nomnom.ai.dto.response.GeneratedDescriptionResponse;
import com.p1.nomnom.ai.service.AiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nom/ai")
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/generate-description")
    public ResponseEntity<GeneratedDescriptionResponse> generateDescription(@RequestBody CreateDescriptionRequest request) {
        GeneratedDescriptionResponse response = aiService.generateDescription(request);
        return ResponseEntity.ok(response);
    }
}
