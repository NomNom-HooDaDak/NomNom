package com.p1.nomnom.store.controller;

import com.p1.nomnom.security.aop.RoleCheck;
import com.p1.nomnom.store.dto.request.StoreRequestDTO;
import com.p1.nomnom.store.dto.response.StoreResponseDTO;
import com.p1.nomnom.store.service.StoreService;
import com.p1.nomnom.user.entity.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid; // jakarta.validation 패키지로 변경
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    // 가게 등록, 가게 주인은 못함
    @RoleCheck({UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @PostMapping
    public ResponseEntity<StoreResponseDTO> createStore(@Valid @RequestBody StoreRequestDTO storeRequestDTO) {
        StoreResponseDTO storeResponseDTO = storeService.createStore(storeRequestDTO);
        return ResponseEntity.ok(storeResponseDTO);
    }

    // 가게 정보 수정
    @RoleCheck({UserRoleEnum.OWNER, UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @PatchMapping("/{storeId}")
    public ResponseEntity<StoreResponseDTO> updateStore(@PathVariable UUID storeId, @Valid @RequestBody StoreRequestDTO storeRequestDTO) {
        StoreResponseDTO storeResponseDTO = storeService.updateStore(storeId, storeRequestDTO);
        return ResponseEntity.ok(storeResponseDTO);
    }

    // 특정 가게 조회
    @RoleCheck({UserRoleEnum.CUSTOMER, UserRoleEnum.OWNER, UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @GetMapping("/{storeId}")
    public ResponseEntity<StoreResponseDTO> getStore(@PathVariable UUID storeId) {
        StoreResponseDTO storeResponseDTO = storeService.getStore(storeId);
        return ResponseEntity.ok(storeResponseDTO);
    }

    // 카테고리별 가게 조회 + 검색, 정렬, 페이지네이션
    @RoleCheck({UserRoleEnum.CUSTOMER, UserRoleEnum.OWNER, UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @GetMapping("/category")
    public ResponseEntity<List<StoreResponseDTO>> getStoresByCategory(
            @RequestParam(value = "category_id") UUID categoryId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder) {

        // Sort 객체를 사용하여 정렬 기준을 처리
        Sort sort = Sort.by("desc".equals(sortOrder) ? Sort.Order.desc(sortBy) : Sort.Order.asc(sortBy));

        // 서비스 호출
        List<StoreResponseDTO> storeList = storeService.getStoresByCategory(categoryId, page, size);
        return ResponseEntity.ok(storeList);
    }

    // 모든 가게 조회 + 검색, 정렬, 페이지네이션
    @RoleCheck({UserRoleEnum.CUSTOMER, UserRoleEnum.OWNER, UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @GetMapping
    public ResponseEntity<List<StoreResponseDTO>> getAllStores(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortOrder", defaultValue = "asc") String sortOrder) {

        // Sort 객체를 사용하여 정렬 기준을 처리
        Sort sort = Sort.by("desc".equals(sortOrder) ? Sort.Order.desc(sortBy) : Sort.Order.asc(sortBy));

        // 서비스 호출
        List<StoreResponseDTO> storeList = storeService.getAllStores(page, size);
        return ResponseEntity.ok(storeList);
    }

    // 가게 숨김 처리
    @RoleCheck({UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @PatchMapping("/{storeId}/hide")
    public ResponseEntity<StoreResponseDTO> hideStore(@PathVariable UUID storeId) {
        StoreResponseDTO storeResponseDTO = storeService.hideStore(storeId); // 서비스에서 반환받은 가게 정보
        return ResponseEntity.ok(storeResponseDTO);  // 숨김 처리된 가게 정보 반환
    }

    //가게 복구
    @RoleCheck({UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @PatchMapping("/{storeId}/restore")
    public ResponseEntity<StoreResponseDTO> restoreStore(@PathVariable UUID storeId) {
        StoreResponseDTO storeResponseDTO = storeService.restoreStore(storeId); // 서비스에서 복구된 가게 정보
        return ResponseEntity.ok(storeResponseDTO);  // 복구된 가게 정보 반환
    }


}
