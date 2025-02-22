package com.p1.nomnom.address.controller;

import com.p1.nomnom.address.dto.request.AddressRequestDto;
import com.p1.nomnom.address.dto.response.AddressResponseDto;
import com.p1.nomnom.address.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/address")
@Tag(name = "주소 API", description = "주소 관련 API")
public class AddressController {

    private final AddressService addressService;
    @Operation(summary = "주소 조회", description = "주소를 조회")
    @ApiResponse(responseCode = "200", description = "주소 조회 성공")
    @GetMapping
    public ResponseEntity<List<AddressResponseDto>> getAddressList(Principal principal) {
        return ResponseEntity.ok(addressService.getAddressList(principal.getName()));
    }

    @Operation(summary = "주소 등록", description = "주소를 등록합니다.")
    @ApiResponse(responseCode = "200", description = "주소 등록 성공")
    @PostMapping
    public ResponseEntity<AddressResponseDto> addAddress(Principal principal, @RequestBody AddressRequestDto requestDto) {
        return ResponseEntity.ok(addressService.addAddress(principal.getName(), requestDto));
    }

    @Operation(summary = "주소 수정", description = "주소를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "주소 수정 성공")
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDto> updateAddress(Principal principal, @PathVariable("addressId") UUID addressId, @RequestBody AddressRequestDto requestDto) {
        return ResponseEntity.ok(addressService.updateAddress(principal.getName(), addressId, requestDto));
    }

    @Operation(summary = "주소 숨김 처리", description = "주소를 삭제하지 않고 숨김 처리합니다.")
    @ApiResponse(responseCode = "200", description = "주소 숨김 처리 완료")
    @PatchMapping("/{addressId}/hide")
    public ResponseEntity<AddressResponseDto> softDeleteAddress(Principal principal, @PathVariable("addressId") UUID addressId) {
        return ResponseEntity.ok(addressService.softDeleteAddress(principal.getName(), addressId));

    }

    @Operation(summary = "삭제된 주소 복구", description = "삭제된 주소를 복구합니다.")
    @ApiResponse(responseCode = "200", description = "주소 복구 성공")
    @PatchMapping("/{addressId}/restore")
    public ResponseEntity<AddressResponseDto> restoreAddress(Principal principal, @PathVariable("addressId") UUID addressId) {
        return ResponseEntity.ok(addressService.restoreAddress(principal.getName(), addressId));
    }

    @Operation(summary = "기본 주소 변경", description = "사용자의 기본 주소를 변경합니다.")
    @ApiResponse(responseCode = "200", description = "기본 주소 변경 성공")
    @PatchMapping("/{addressId}/default")
    public ResponseEntity<AddressResponseDto> updateDefaultAddress(Principal principal, @PathVariable("addressId") UUID addressId) {
        return ResponseEntity.ok(addressService.updateDefaultAddress(principal.getName(), addressId));
    }

}
