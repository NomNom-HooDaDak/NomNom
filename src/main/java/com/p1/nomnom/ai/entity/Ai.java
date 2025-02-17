package com.p1.nomnom.ai.entity;

import com.p1.nomnom.common.entity.BaseEntity;
import jakarta.persistence.*;
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
    private String question;

    @Column(name = "answer", nullable = false, columnDefinition = "TEXT")
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
