package com.p1.nomnom.ai.dto.request;

import java.util.UUID;

public class CreateDescriptionRequest {
    private String foodName;        // 음식 이름
    private UUID storeId;           // 가게 ID
    private String question;        // 질문
    private String descriptionHint; // 설명 힌트
    private String keyword;         // 키워드

    // Getters and Setters
    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public UUID getStoreId() {
        return storeId;
    }

    public void setStoreId(UUID storeId) {
        this.storeId = storeId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDescriptionHint() {
        return descriptionHint;
    }

    public void setDescriptionHint(String descriptionHint) {
        this.descriptionHint = descriptionHint;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
