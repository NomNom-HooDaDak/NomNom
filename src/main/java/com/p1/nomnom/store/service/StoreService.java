package com.p1.nomnom.store.service;

import com.p1.nomnom.store.dto.request.StoreRequestDTO;
import com.p1.nomnom.store.dto.response.StoreResponseDTO;
import com.p1.nomnom.store.entity.Store;
import com.p1.nomnom.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    // 검색, 페이지네이션 및 정렬 기능 구현
    @Transactional
    public List<StoreResponseDTO> searchStores(UUID categoryId, int page, int size, String sortBy) {
        // 기본적으로 페이지 크기를 10으로 설정
        if (size != 10 && size != 30 && size != 50) {
            size = 10; // 10건씩 기본 설정
        }

        Page<Store> storesPage = storeRepository.searchStores(categoryId, page, size, sortBy);
        return storesPage.stream()
                .map(store -> new StoreResponseDTO(store.getId(), store.getName(), store.getAddress(),
                        store.getPhone(), store.getOpenTime(), store.getCloseTime(), store.getCategory().getId(), store.getCreatedAt()))
                .collect(Collectors.toList());
    }
}
