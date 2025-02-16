package com.p1.nomnom.store.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class StoreResponseDTO {

    private UUID id;
    private String name;
    private String address;
    private String phone;
    private String openTime;
    private String closeTime;
    private UUID categoryId;
    private LocalDateTime createdAt;
    private boolean hidden;  // 숨김 처리 여부
    private LocalDateTime deletedAt; // 삭제 시간

    public StoreResponseDTO(UUID id, String name, String address, String phone, String openTime, String closeTime, UUID categoryId, LocalDateTime createdAt, boolean hidden, LocalDateTime deletedAt) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.categoryId = categoryId;
        this.createdAt = createdAt;
        this.hidden = hidden;
        this.deletedAt = deletedAt;
    }
}
