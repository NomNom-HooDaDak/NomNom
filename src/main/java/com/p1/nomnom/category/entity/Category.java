package com.p1.nomnom.category.entity;

import com.p1.nomnom.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "p_category", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private Boolean hidden = false;

    // 카테고리 숨김 처리 (삭제 처리)
    public void hide(String deletedBy) {
        this.hidden = true;  // hidden을 true로 설정하여 숨김 처리
        this.markAsDeleted(deletedBy); // 삭제일 및 삭제자 설정
    }

    // 카테고리 복구 처리
    public void restoreCategory(String updatedBy) {
        this.hidden = false;  // hidden을 false로 설정하여 복구
        this.unhide(updatedBy); // 삭제일 및 삭제자 초기화, 수정일 및 수정자 설정
    }
}
