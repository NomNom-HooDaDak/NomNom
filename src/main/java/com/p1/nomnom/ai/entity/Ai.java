package com.p1.nomnom.ai.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "p_ai")
public class Ai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "question", nullable = false)
    private String question;

    @Column(name = "answer", nullable = false)
    private String answer;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "description_hint")
    private String descriptionHint;

    @Column(name = "keyword")
    private String keyword;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getStoreId() {
        return storeId;
    }

    public void setStoreId(Long storeId) {
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
