package com.p1.nomnom.ai.entity;

import com.p1.nomnom.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "p_ai")
public class Ai extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "question", nullable = false, columnDefinition = "TEXT")
    @Size(max = 500, message = "질문은 최대 500자까지 입력 가능합니다.")
    private String question;

    @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
    @Size(max = 255, message = "답변은 최대 255자까지 입력 가능합니다.")
    private String answer;

    @Column(name = "food_name", nullable = false, columnDefinition = "TEXT")
    private String foodName;

    @Column(name = "store_id")
    private UUID storeId;

    @Column(name = "description_hint", columnDefinition = "TEXT")
    private String descriptionHint;

    @Column(name = "keyword", columnDefinition = "TEXT")
    private String keyword;
}
