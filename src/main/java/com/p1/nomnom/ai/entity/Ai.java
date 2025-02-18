package com.p1.nomnom.ai.entity;

import com.p1.nomnom.common.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor  //Builder패턴 생성자 추가
@NoArgsConstructor  //기본 생성자 추가(JPA)
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

    @Column(name = "hidden", nullable = false)
    private Boolean hidden = false;

    @Column(name = "generated_description", columnDefinition = "TEXT")
    private String generatedDescription;  // AI에서 받은 원본 JSON

    //AI 응답 숨김 처리 (BaseEntity 기능 활용)
    public void hide(String deletedBy) {
        this.hidden = true;  // 숨김 처리 활성화
        this.markAsDeleted(deletedBy); // BaseEntity의 삭제 관리 메서드 호출
    }

    //AI 응답 복구 처리 (BaseEntity 기능 활용)
    public void restore(String updatedBy) {
        this.hidden = false;  // 숨김 처리 해제
        this.unhide(updatedBy); // BaseEntity의 복구 관리 메서드 호출
    }
}
