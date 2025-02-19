package com.p1.nomnom.food.service;

import com.p1.nomnom.food.dto.request.FoodRequestDto;
import com.p1.nomnom.food.dto.response.FoodResponseDto;
import com.p1.nomnom.food.entity.Food;
import com.p1.nomnom.food.repository.FoodRepository;
import com.p1.nomnom.store.entity.Store;
import com.p1.nomnom.store.repository.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FoodService {
    private final FoodRepository foodRepository;
    private final StoreRepository storeRepository;


    @Transactional
    public FoodResponseDto createFood(Store store, FoodRequestDto requestDto) {
         try {
            Food food = new Food(store, requestDto);
            food.createBy("박성주");
            Food savedFood = foodRepository.save(food);
            return new FoodResponseDto(savedFood);
         } catch(Exception e) {
             e.getStackTrace();
             throw new RuntimeException(requestDto.getName() + " 메뉴 등록에 실패하였습니다.", e);
        }
    }

    public List<FoodResponseDto> findAll(String storeId) {
        // 1. Store 존재하는지 확인
        Store findStore = getFindStore(storeId);
        List<Food> allFoodListByStore = foodRepository.findAllFoodListByStore(findStore);
        return allFoodListByStore
                .stream()
                .map(FoodResponseDto::new)// (food-> new FoodResponseDto(food))
                .toList(); // .collect(Collectors.toList()); --> ????

         /* storeId가 String 타입일 때
        if(!foodList.isEmpty()) { // foodList.size() == 0
            return foodList
                    .stream()
                    .filter(food->food.getStoreId().equals(storeId))
                    .map(FoodResponseDto::new)// (food-> new FoodResponseDto(food))
                    .toList();
                    */

        /*
            List<FoodResponseDto> foodResponseDtoList = new ArrayList<>();
            for(Food food: foodList) {
                if(food.getName().equals(storeId)) {
                    foodResponseDtoList.add(new FoodResponseDto(food));
                }
            }
            return foodResponseDtoList;
        */

    }

    /* 가게가 존재하는지 확인하는 메서드 */
    public Store getFindStore(String storeId) {
        UUID uuid = UUID.fromString(storeId);

        return storeRepository.findById(uuid).orElseThrow(() ->
                new IllegalArgumentException("가게가 존재하지 않습니다. 가게를 먼저 생성해주세요!")
        );
    }

    // 특정 가게의 특정 음식을 조회할 때
    public FoodResponseDto findFood(String storeId, String foodId) {
        // 1. Store 테이블에 storeId 가 존재하는지 확인한다.
        Store findStore = getFindStore(storeId);

        // 2. Store 객체와 food Id 를 전달하면서 특정 음식 데이터를 전달받기 join --> where f.food_id = foodId;
        // Food 객체를 얻기 위해 String 타입의 foodId 를 UUID 타입으로 바꾼후 findById 로 조회하기
        UUID foodUUID = UUID.fromString(foodId);

        Food food = foodRepository.findById(foodUUID).orElseThrow(()->
                new IllegalArgumentException("잘못된 접근 경로입니다.")
        );

        if(!findStore.getId().equals(food.getStore().getId())) {
            // Store 객체의 id 와 Food Entity 에 저장된 Store id 가 동일하지 않다면? 이런 경우가 있나?
            // --> 해당 가게가 등록한 음식이 아닌 경우
            throw new IllegalArgumentException("잘못된 접근 경로입니다.");
        }
        return new FoodResponseDto(food);

    }

    @Transactional
    // 가게 주인이 (등록한) 특정 음식 정보를(상세 사항을) 변경할 때
    public FoodResponseDto updateFoodInfo(String storeId, String foodId, FoodRequestDto updateRequestDto) {
        // 1. Store 테이블에 storeId 가 존재하는지 확인한다.
        Store findStore = getFindStore(storeId);

        // 2. foodId 로 Food 객체를 얻기
        UUID foodUUID = UUID.fromString(foodId);
        Food food = foodRepository.findById(foodUUID).orElseThrow(()->
                new IllegalArgumentException("잘못된 접근 경로입니다.")
        );

        // 3. Store 와 Food 간 관계 확인 --> 해당 Store 에 등록된 메뉴가 맞는지
        if(!findStore.getId().equals(food.getStore().getId())) {
            throw new IllegalArgumentException("잘못된 접근 경로입니다.");
        }
        // --> 내용 비교 --> 변경사항 있는 항목 변경하기
        if(updateRequestDto.getName() != null && !(updateRequestDto.getName().isBlank()) && !food.getName().equals(updateRequestDto.getName())) {
            food.setName(updateRequestDto.getName());
        }

        if(updateRequestDto.getDescription() != null
                && !updateRequestDto.getDescription().equals(food.getDescription())) {
            food.setDescription(updateRequestDto.getDescription());
        }

        if (updateRequestDto.getPrice() != null && !food.getPrice().equals(updateRequestDto.getPrice())) {
            food.setPrice(updateRequestDto.getPrice());
        }

        if(updateRequestDto.getImage() != null && !updateRequestDto.getImage().equals(food.getImage())) {
            food.setImage(updateRequestDto.getImage());
        }

        food.setUpdatedBy("OWNER"); // User 객체를 받아서 userName 을 전달하기

        return new FoodResponseDto(foodRepository.save(food));
    }

    // 가게주인이 (등록되어 있는) 하나의 메뉴를 삭제하는 기능
    public FoodResponseDto hideOneMenu(String storeId, String foodId) {
        // 1. Store 테이블에 storeId 가 존재하는지 확인한다.
        Store findStore = getFindStore(storeId);

        // 2. foodId 로 Food 객체를 얻기
        UUID foodUUID = UUID.fromString(foodId);
        Food food = foodRepository.findById(foodUUID).orElseThrow(()->
                new IllegalArgumentException("잘못된 접근 경로입니다.")
        );

        // 3. Store 와 Food 간 관계 확인 --> 해당 Store 에 등록된 메뉴가 맞는지
        if(!findStore.getId().equals(food.getStore().getId())) {
            throw new IllegalArgumentException("잘못된 접근 경로입니다.");
        }

        // 가게 주인의 객체(User) 를 받아서
        // use.getName() 을 전달한다.
        food.hide("OWNER");

        return new FoodResponseDto(foodRepository.save(food));
    }

    public List<Food> getFoodsByIds(List<UUID> foodIds) {
        return foodRepository.findAllById(foodIds);
    }
}