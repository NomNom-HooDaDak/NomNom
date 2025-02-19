package com.p1.nomnom.ai.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AiRequestDto {
    private String question;
    private String foodName;
    private String descriptionHint;
    private String keyword;
    private UUID storeId;  // storeId 추가
}

