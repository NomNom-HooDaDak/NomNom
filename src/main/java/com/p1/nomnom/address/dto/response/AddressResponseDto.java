package com.p1.nomnom.address.dto.response;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponseDto {
    private UUID id;
    private String address;
    private boolean isDefault;
}
