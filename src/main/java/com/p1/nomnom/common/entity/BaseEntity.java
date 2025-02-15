package com.p1.nomnom.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity {
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    @Comment("생성일")
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    @Comment("생성자")
    private String createdBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    @Comment("수정일")
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(name = "updated_by")
    @Comment("수정자")
    private String updatedBy;

    @Column(name = "deleted_at")
    @Comment("삭제일")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    @Comment("삭제자")
    private String deletedBy;
}
