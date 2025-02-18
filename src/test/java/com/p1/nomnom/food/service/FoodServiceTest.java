package com.p1.nomnom.food.service;

import com.p1.nomnom.food.dto.request.FoodRequestDto;
import com.p1.nomnom.food.dto.response.FoodResponseDto;
import com.p1.nomnom.food.entity.Food;
import com.p1.nomnom.food.repository.FoodRepository;
import com.p1.nomnom.store.entity.Store;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@Transactional
@Slf4j(topic="food_test")
class FoodServiceTest {
    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private FoodService foodService;

    private final String uuidString = "ec392e45-c668-42a7-a616-bf67dc5df362";
    private final String uuidFoodString = "7611993e-3d1a-4585-b05d-8038f1d77465";

    @Test
    @DisplayName("(한 명의) 가게 주인이 음식 메뉴 한 개를 등록하기")
    // @Transactional
    @Rollback(value = false)
    void test1() {
        // given
        // String uuidString = "ec392e45-c668-42a7-a616-bf67dc5df362"; // 받은 문자열
        // UUID uuid = UUID.fromString(uuidString); // String을 UUID로 변환

        Store findStore = foodService.getFindStore(uuidString);

        FoodRequestDto foodRequestDto = new FoodRequestDto();
        foodRequestDto.setName("후라이드 치킨");
        foodRequestDto.setPrice("10000");
        foodRequestDto.setDescription("너무너무 맛있어요");
        foodRequestDto.setImage("https://store.google.com/regionpicker?hl=en-US");
        // when
        Food food = foodRepository.save(new Food(findStore, foodRequestDto));
        // then
        log.info("findStore 이름: {}", findStore.getName());
        log.info("food Entity 객체 내용보기: "+food.toString());
        log.info("foodRequestDto(클라이언트로부터 받아온 데이터)에서 등록음식명 보기: "+foodRequestDto.getName());
        log.info("food(DB에 저장하고 반환된 Entity 객체?)에서 등록된 음식명 보기: "+food.getName());
        Assertions.assertEquals(foodRequestDto.getName(), food.getName());
        Assertions.assertEquals("Burger King", findStore.getName());

    }

    @Test
    @DisplayName("(한 명의) 가게 주인이 음식 메뉴 여러 개 등록하기")
    @Rollback(value = false)
    void test2() {
        // given
        // String uuidString = "ec392e45-c668-42a7-a616-bf67dc5df362"; // 받은 문자열

        Store findStore = foodService.getFindStore(uuidString);

        for(int i=0; i<=10; i++) {
            FoodRequestDto foodRequestDto = new FoodRequestDto();
            foodRequestDto.setName("후라이드 치킨"+i);
            foodRequestDto.setPrice("10000"+i);
            foodRequestDto.setDescription("너무너무 맛있어요"+i);
            foodRequestDto.setImage("https://store.google.com/regionpicker?hl=en-US"+i);

            // when
            foodRepository.save(new Food(findStore, foodRequestDto));
        }

        List<Food> allFoodListByStore = foodRepository.findAllFoodListByStore(findStore);
        log.info("uuid가 \"ec392e45-c668-42a7-a616-bf67dc5df362\" 인 가게가 등록한 메뉴의 갯수는 "+allFoodListByStore.size());

        // then
        Assertions.assertEquals(12, allFoodListByStore.size());
    }

    @Test
    @DisplayName("특정 가게가 등록한 모든 음식 목록 조회하기")
    void test3() {
        // given
        // String storeId = "ec392e45-c668-42a7-a616-bf67dc5df362";

        // when
        List<FoodResponseDto> foodList = foodService.findAll(uuidString);
        foodList.forEach(food->log.info(food.toString()));

        // then
        Assertions.assertEquals(12, foodList.size());
    }

    @Test
    @DisplayName("특정 가게가 (등록했던) 하나의 메뉴 정보를 수정하기")
    @Transactional
    @Rollback(value = false)
    void test4() {
        // given
        // String foodId = "7611993e-3d1a-4585-b05d-8038f1d77465";

        FoodRequestDto foodRequestDto = new FoodRequestDto();
        foodRequestDto.setName("변경했다.");
        foodRequestDto.setPrice("많이비쌈");
        foodRequestDto.setDescription("과연");
        foodRequestDto.setImage("들어갈것인가");

        // when
        log.info("변경전 데이터 조회: {}", foodService.findFood(uuidString, uuidFoodString));
        log.info("변경할 데이터 전달: {}", foodRequestDto.toString());
        FoodResponseDto foodResponseDto = foodService.updateFoodInfo(uuidString, uuidFoodString, foodRequestDto);
        log.info("변경후 데이터 전달: {}", foodResponseDto.toString());

        // then
        Assertions.assertEquals(foodRequestDto.getName(), foodResponseDto.getName());
    }

    @Test
    @DisplayName("특정 가게가 (등록했던) 하나의 메뉴를 숨김처리한다.")
    @Rollback(value = false)
    void test5() {
        // given
        // String foodId = "7611993e-3d1a-4585-b05d-8038f1d77465";

        // when
        FoodResponseDto foodResponseDto = foodService.hideOneMenu(uuidString, uuidFoodString);
        Food food = foodRepository.findById(UUID.fromString(uuidFoodString)).orElse(null);

        // then
        Assertions.assertEquals(true, food.getHidden());
    }
}