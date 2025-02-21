package com.p1.nomnom.store.service;

import com.p1.nomnom.security.aop.UserContext;
import com.p1.nomnom.store.dto.request.StoreRequestDTO;
import com.p1.nomnom.store.dto.response.StoreResponseDTO;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.UUID;

public interface StoreService {
    // 검색, 페이지네이션 및 정렬 기능 구현
    List<StoreResponseDTO> searchStores(UUID categoryId, String name, int pageSize, Sort sort);

    // 가게 등록
    StoreResponseDTO createStore(StoreRequestDTO storeRequestDTO, UserContext userContext);

    // 가게 정보 수정
    StoreResponseDTO updateStore(UUID storeId, StoreRequestDTO storeRequestDTO,  UserContext userContext);

    // 특정 가게 조회
    StoreResponseDTO getStore(UUID storeId);

    // 카테고리별 가게 조회
    List<StoreResponseDTO> getStoresByCategory(UUID categoryId, int page, int size);

    // 모든 가게 조회
    List<StoreResponseDTO> getAllStores(int page, int size);

    // 가게 숨김 처리
    StoreResponseDTO hideStore(UUID storeId, UserContext userContext);

    // 가게 복구 처리
    StoreResponseDTO restoreStore(UUID storeId, UserContext userContext);

    // feature_ai 추가부분
    String getStoreNameById(UUID storeId);
}

