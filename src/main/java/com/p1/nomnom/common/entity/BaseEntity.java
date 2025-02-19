package com.p1.nomnom.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED) // 기본 생성자
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    @Comment("생성일")
    protected LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    @Comment("생성자")
    protected String createdBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    @Comment("수정일")
    protected LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    @Comment("수정자")
    protected String updatedBy;

    @Column(name = "deleted_at")
    @Comment("삭제일")
    protected LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    @Comment("삭제자")
    protected String deletedBy;

    // hide 삭제 대신 숨김
    public void markAsDeleted(String deletedBy) {
        this.deletedAt = LocalDateTime.now(); // 삭제일 설정
        this.deletedBy = deletedBy;          // 삭제자 설정
    }

    // unhide 메서드 수정: 삭제 상태를 해제 (복구)
    public void unhide(String updatedBy) {
        this.deletedAt = null;   // 삭제일 초기화
        this.deletedBy = null;   // 삭제자 초기화
        this.updatedAt = LocalDateTime.now(); // 수정일을 현재 시간으로 설정
        this.updatedBy = updatedBy; // 수정자를 지정한 값으로 설정
    }
}
