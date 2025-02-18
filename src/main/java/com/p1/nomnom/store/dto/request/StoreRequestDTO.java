package com.p1.nomnom.store.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class StoreRequestDTO {

    @NotEmpty(message = "가게 이름은 필수입니다.")
    @Size(min = 1, max = 255, message = "가게 이름은 1자 이상 255자 이하로 설정해주세요.")
    private String name;

    @NotEmpty(message = "가게 주소는 필수입니다.")
    private String address;

    @NotEmpty(message = "가게 전화번호는 필수입니다.")
    private String phone;

    @NotEmpty(message = "오픈 시간은 필수입니다.")
    private String openTime;

    @NotEmpty(message = "마감 시간은 필수입니다.")
    private String closeTime;

    @NotNull(message = "카테고리 ID는 필수입니다.")
    private UUID categoryId;

    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;
}
