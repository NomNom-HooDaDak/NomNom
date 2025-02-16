package com.p1.nomnom.store.service;

import com.p1.nomnom.store.dto.request.StoreRequestDTO;
import com.p1.nomnom.store.dto.response.StoreResponseDTO;
import com.p1.nomnom.store.entity.Store;
import com.p1.nomnom.store.repository.StoreRepository;
import com.p1.nomnom.category.entity.Category;
import com.p1.nomnom.category.repository.CategoryRepository;
import com.p1.nomnom.user.entity.User;
import com.p1.nomnom.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public StoreResponseDTO createStore(StoreRequestDTO storeRequestDTO) {
        // 카테고리 및 사용자 확인
        Category category = categoryRepository.findById(storeRequestDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));
        User user = userRepository.findById(storeRequestDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

        // 가게 생성
        Store store = new Store();
        store.setName(storeRequestDTO.getName());
        store.setAddress(storeRequestDTO.getAddress());
        store.setPhone(storeRequestDTO.getPhone());
        store.setOpenTime(storeRequestDTO.getOpenTime());
        store.setCloseTime(storeRequestDTO.getCloseTime());
        store.setCategory(category);
        store.setUser(user);

        storeRepository.save(store);

        // 응답 DTO 반환
        StoreResponseDTO storeResponseDTO = new StoreResponseDTO();
        storeResponseDTO.setId(store.getId());
        storeResponseDTO.setName(store.getName());
        storeResponseDTO.setAddress(store.getAddress());
        storeResponseDTO.setPhone(store.getPhone());
        storeResponseDTO.setOpenTime(store.getOpenTime());
        storeResponseDTO.setCloseTime(store.getCloseTime());
        storeResponseDTO.setCategoryId(store.getCategory().getId());
        storeResponseDTO.setCreatedAt(store.getCreatedAt());

        return storeResponseDTO;
    }

    @Override
    @Transactional
    public StoreResponseDTO updateStore(UUID storeId, StoreRequestDTO storeRequestDTO) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게가 존재하지 않습니다."));

        Category category = categoryRepository.findById(storeRequestDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));

        store.setName(storeRequestDTO.getName());
        store.setAddress(storeRequestDTO.getAddress());
        store.setPhone(storeRequestDTO.getPhone());
        store.setOpenTime(storeRequestDTO.getOpenTime());
        store.setCloseTime(storeRequestDTO.getCloseTime());
        store.setCategory(category);

        storeRepository.save(store);

        StoreResponseDTO storeResponseDTO = new StoreResponseDTO();
        storeResponseDTO.setId(store.getId());
        storeResponseDTO.setName(store.getName());
        storeResponseDTO.setAddress(store.getAddress());
        storeResponseDTO.setPhone(store.getPhone());
        storeResponseDTO.setOpenTime(store.getOpenTime());
        storeResponseDTO.setCloseTime(store.getCloseTime());
        storeResponseDTO.setCategoryId(store.getCategory().getId());
        storeResponseDTO.setCreatedAt(store.getCreatedAt());

        return storeResponseDTO;
    }

    @Override
    public StoreResponseDTO getStore(UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게가 존재하지 않습니다."));

        StoreResponseDTO storeResponseDTO = new StoreResponseDTO();
        storeResponseDTO.setId(store.getId());
        storeResponseDTO.setName(store.getName());
        storeResponseDTO.setAddress(store.getAddress());
        storeResponseDTO.setPhone(store.getPhone());
        storeResponseDTO.setOpenTime(store.getOpenTime());
        storeResponseDTO.setCloseTime(store.getCloseTime());
        storeResponseDTO.setCategoryId(store.getCategory().getId());
        storeResponseDTO.setCreatedAt(store.getCreatedAt());

        return storeResponseDTO;
    }

    @Override
    public List<StoreResponseDTO> getAllStores(UUID categoryId, int page, int size) {
        List<Store> stores = storeRepository.findAllByCategoryId(categoryId, page, size);

        return stores.stream().map(store -> {
            StoreResponseDTO storeResponseDTO = new StoreResponseDTO();
            storeResponseDTO.setId(store.getId());
            storeResponseDTO.setName(store.getName());
            storeResponseDTO.setAddress(store.getAddress());
            storeResponseDTO.setPhone(store.getPhone());
            storeResponseDTO.setOpenTime(store.getOpenTime());
            storeResponseDTO.setCloseTime(store.getCloseTime());
            storeResponseDTO.setCategoryId(store.getCategory().getId());
            storeResponseDTO.setCreatedAt(store.getCreatedAt());
            return storeResponseDTO;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void hideStore(UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게가 존재하지 않습니다."));
        store.hide("관리자");
        storeRepository.save(store);
    }
}
