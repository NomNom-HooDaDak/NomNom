package com.p1.nomnom.store.service;

import com.p1.nomnom.category.entity.Category;
import com.p1.nomnom.category.repository.CategoryRepository;
import com.p1.nomnom.store.dto.request.StoreRequestDTO;
import com.p1.nomnom.store.dto.response.StoreResponseDTO;
import com.p1.nomnom.store.entity.Store;
import com.p1.nomnom.store.repository.StoreRepository;
import com.p1.nomnom.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    // 검색, 페이지네이션 및 정렬 기능 구현
    @Override
    @Transactional
    public List<StoreResponseDTO> searchStores(UUID categoryId, String name, int pageSize, Sort sort) {
        Pageable pageable = PageRequest.of(0, pageSize, sort); // 첫 페이지, 지정된 페이지 사이즈, 정렬
        Page<Store> pageResult;

        if (name != null && !name.isEmpty()) {
            pageResult = storeRepository.findByNameContainingAndCategoryId(name, categoryId, pageable);
        } else {
            pageResult = storeRepository.findAllByCategoryId(categoryId, pageable);
        }

        return pageResult.stream()
                .map(store -> new StoreResponseDTO(store.getId(), store.getName(), store.getAddress(),
                        store.getPhone(), store.getOpenTime(), store.getCloseTime(), store.getCategory().getId(),
                        store.getCreatedAt(), store.getHidden(), store.getDeletedAt()))
                .collect(Collectors.toList());
    }

    // 가게 등록
    @Override
    @Transactional
    public StoreResponseDTO createStore(StoreRequestDTO storeRequestDTO) {
        // 카테고리 확인
        Category category = categoryRepository.findById(storeRequestDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));

        // 사용자 정보 입력 받기 (예: 가게 사장 이름 입력)
        User user = new User();
        user.setId(storeRequestDTO.getUserId()); // 사용자 ID가 입력됨

        // 가게 생성
        Store store = new Store();
        store.setName(storeRequestDTO.getName());
        store.setAddress(storeRequestDTO.getAddress());
        store.setPhone(storeRequestDTO.getPhone());
        store.setOpenTime(storeRequestDTO.getOpenTime());
        store.setCloseTime(storeRequestDTO.getCloseTime());
        store.setCategory(category);
        store.setUser(user); // 직접 입력 받은 User 객체 설정

        storeRepository.save(store);

        // 응답 DTO 반환
        return new StoreResponseDTO(store.getId(), store.getName(), store.getAddress(),
                store.getPhone(), store.getOpenTime(), store.getCloseTime(), store.getCategory().getId(),
                store.getCreatedAt(), store.getHidden(), store.getDeletedAt());
    }

    // 가게 정보 수정
    @Override
    @Transactional
    public StoreResponseDTO updateStore(UUID storeId, StoreRequestDTO storeRequestDTO) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게가 존재하지 않습니다."));

        Category category = categoryRepository.findById(storeRequestDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리가 존재하지 않습니다."));

        // 사용자 정보 직접 설정 (예: 가게 사장 이름 입력)
        User user = new User();
        user.setId(storeRequestDTO.getUserId()); // 사용자 ID가 입력됨

        store.setName(storeRequestDTO.getName());
        store.setAddress(storeRequestDTO.getAddress());
        store.setPhone(storeRequestDTO.getPhone());
        store.setOpenTime(storeRequestDTO.getOpenTime());
        store.setCloseTime(storeRequestDTO.getCloseTime());
        store.setCategory(category);
        store.setUser(user); // 직접 입력 받은 User 객체 설정

        storeRepository.save(store);

        // 응답 DTO 반환
        return new StoreResponseDTO(store.getId(), store.getName(), store.getAddress(),
                store.getPhone(), store.getOpenTime(), store.getCloseTime(), store.getCategory().getId(),
                store.getCreatedAt(), store.getHidden(), store.getDeletedAt());
    }

    // 특정 가게 조회
    @Override
    public StoreResponseDTO getStore(UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게가 존재하지 않습니다."));

        // 응답 DTO 반환
        return new StoreResponseDTO(store.getId(), store.getName(), store.getAddress(),
                store.getPhone(), store.getOpenTime(), store.getCloseTime(), store.getCategory().getId(),
                store.getCreatedAt(), store.getHidden(), store.getDeletedAt());
    }

    // 카테고리별 가게 조회
    @Override
    public List<StoreResponseDTO> getStoresByCategory(UUID categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);  // Pageable 객체 생성
        Page<Store> storesPage = storeRepository.findAllByCategoryId(categoryId, pageable);

        return storesPage.stream()
                .map(store -> new StoreResponseDTO(store.getId(), store.getName(), store.getAddress(),
                        store.getPhone(), store.getOpenTime(), store.getCloseTime(), store.getCategory().getId(),
                        store.getCreatedAt(), store.getHidden(), store.getDeletedAt()))
                .collect(Collectors.toList());
    }

    // 모든 가게 조회
    @Override
    public List<StoreResponseDTO> getAllStores(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);  // Pageable 객체 생성
        Page<Store> storesPage = storeRepository.findAll(pageable);

        return storesPage.stream()
                .map(store -> new StoreResponseDTO(store.getId(), store.getName(), store.getAddress(),
                        store.getPhone(), store.getOpenTime(), store.getCloseTime(), store.getCategory().getId(),
                        store.getCreatedAt(), store.getHidden(), store.getDeletedAt()))
                .collect(Collectors.toList());
    }

    // 가게 숨김 처리
    @Override
    @Transactional
    public StoreResponseDTO hideStore(UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게가 존재하지 않습니다."));
        store.hide("관리자");  // 가게 숨김 처리
        storeRepository.save(store);

        // 숨김 처리된 가게의 모든 정보를 반환
        return new StoreResponseDTO(store.getId(), store.getName(), store.getAddress(),
                store.getPhone(), store.getOpenTime(), store.getCloseTime(), store.getCategory().getId(),
                store.getCreatedAt(), store.getHidden(), store.getDeletedAt()); // 수정된 부분
    }
    //가게 복구
    @Override
    @Transactional
    public StoreResponseDTO restoreStore(UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게가 존재하지 않습니다."));

        // 가게 복구 처리
        store.restoreStore("관리자"); // 복구 작업
        storeRepository.save(store);

        // 복구된 가게의 모든 정보를 반환
        return new StoreResponseDTO(store.getId(), store.getName(), store.getAddress(),
                store.getPhone(), store.getOpenTime(), store.getCloseTime(), store.getCategory().getId(),
                store.getCreatedAt(), store.getHidden(), store.getDeletedAt());
    }

    // feature_ai 추가부분
    @Override
    public String getStoreNameById(UUID storeId) {
        // storeId로 Store 정보 조회
        Optional<Store> storeOptional = storeRepository.findById(storeId);

        if (storeOptional.isPresent()) {
            return storeOptional.get().getName();  // 데이터베이스에서 찾은 storeName 반환
        }
        return "알 수 없음";

    }
}
