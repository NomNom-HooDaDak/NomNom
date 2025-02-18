package com.p1.nomnom.ai.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "p_ai")
public class Ai {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "question", nullable = false, length = 255)
    private String question;

    @Column(name = "answer", nullable = false, length = 255)
    private String answer;

    @Column(name = "food_name", nullable = false, length = 255)
    private String foodName;

    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    @Column(name = "description_hint", length = 255)
    private String descriptionHint;

    @Column(name = "keyword", length = 255)
    private String keyword;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

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
