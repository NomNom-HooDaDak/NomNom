package com.p1.nomnom.category.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CategoryResponseDTO {

    private UUID id;
    private String name;
    private String description;
    private Boolean hidden;
    private LocalDateTime createdAt;  // 추가
    private LocalDateTime updatedAt;  // 추가
    private LocalDateTime deletedAt;  // 추가
    private String deletedBy;        // 추가

}