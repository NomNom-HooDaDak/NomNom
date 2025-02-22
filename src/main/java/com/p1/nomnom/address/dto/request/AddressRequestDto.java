package com.p1.nomnom.address.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AddressRequestDto {
    private String address;
    private boolean isDefault;
}
