package com.p1.nomnom.store.controller;

import com.p1.nomnom.store.dto.request.StoreRequestDTO;
import com.p1.nomnom.store.dto.response.StoreResponseDTO;
import com.p1.nomnom.store.service.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/nom/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    // 가게 등록
    @PostMapping
    public ResponseEntity<StoreResponseDTO> createStore(@Valid @RequestBody StoreRequestDTO storeRequestDTO) {
        StoreResponseDTO storeResponseDTO = storeService.createStore(storeRequestDTO);
        return new ResponseEntity<>(storeResponseDTO, HttpStatus.CREATED);
    }

    // 가게 정보 수정
    @PatchMapping("/{storeId}")
    public ResponseEntity<StoreResponseDTO> updateStore(@PathVariable UUID storeId, @Valid @RequestBody StoreRequestDTO storeRequestDTO) {
        StoreResponseDTO storeResponseDTO = storeService.updateStore(storeId, storeRequestDTO);
        return ResponseEntity.ok(storeResponseDTO);
    }

    // 특정 가게 조회
    @GetMapping("/{storeId}")
    public ResponseEntity<StoreResponseDTO> getStore(@PathVariable UUID storeId) {
        StoreResponseDTO storeResponseDTO = storeService.getStore(storeId);
        return ResponseEntity.ok(storeResponseDTO);
    }

    // 모든 가게 조회 + 검색, 정렬, 페이지네이션
    @GetMapping
    public ResponseEntity<List<StoreResponseDTO>> getAllStores(
            @RequestParam(value = "category_id", required = false) UUID categoryId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "updatedAt") String sortBy) {

        List<StoreResponseDTO> storeList = storeService.searchStores(categoryId, page, size, sortBy);
        return ResponseEntity.ok(storeList);
    }

    // 가게 숨김 처리
    @PatchMapping("/{storeId}/hide")
    public ResponseEntity<String> hideStore(@PathVariable UUID storeId) {
        storeService.hideStore(storeId);
        return ResponseEntity.ok("가게가 숨김 처리되었습니다.");
    }
}
