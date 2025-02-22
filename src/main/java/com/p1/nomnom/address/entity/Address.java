package com.p1.nomnom.address.entity;

import com.p1.nomnom.common.entity.BaseEntity;
import com.p1.nomnom.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_addresses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Address extends BaseEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String address;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false; // 삭제 여부 추가 (Soft Delete)

    @PrePersist
    public void generateUUID() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

    public void updateAddress(String newAddress, boolean newDefault) {
        if (!this.address.equals(newAddress) || this.isDefault != newDefault) {
            this.address = newAddress;
            this.isDefault = newDefault;
        }
    }

    public void softDelete() {
        if (!this.isDeleted) {
            this.isDeleted = true;
        }
    }

    public void restore() {
        if (this.isDeleted) {
            this.isDeleted = false;
        }
    }

    // 기본 주소 설정 변경 메서드
    public void setAsDefault() {
        this.isDefault = true;
    }

    public void unsetDefault() {
        this.isDefault = false;
    }


}
