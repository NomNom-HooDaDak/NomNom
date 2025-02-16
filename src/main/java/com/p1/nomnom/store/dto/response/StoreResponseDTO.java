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

    // Store 엔티티로부터 생성된 DTO
    public StoreResponseDTO(UUID id, String name, String address, String phone, String openTime, String closeTime, UUID categoryId, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.categoryId = categoryId;
        this.createdAt = createdAt;
    }
}
