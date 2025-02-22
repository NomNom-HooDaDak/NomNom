package com.p1.nomnom.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RoleUpdateRequestDto {

    @NotBlank
    private String username;

    @NotBlank
    private String newRole;
}
