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
}
