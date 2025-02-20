package com.p1.nomnom.food.controller;

import com.p1.nomnom.ai.service.AiService;
import com.p1.nomnom.food.dto.request.FoodRequestDto;
import com.p1.nomnom.food.dto.response.FoodResponseDto;
import com.p1.nomnom.food.service.FoodService;
import com.p1.nomnom.security.aop.RoleCheck;
import com.p1.nomnom.store.entity.Store;
import com.p1.nomnom.user.entity.UserRoleEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // @Controller + @ResponseBody
@RequestMapping("/api/food")
@RequiredArgsConstructor
@Slf4j(topic="FoodController")
public class FoodController {
    private final FoodService foodService;
    private final AiService aiService;

    // http://localhost:8080/api/food/11111111-1111-1111-1111-111111111111/create
    @PostMapping("/{storeId}/create")
    @RoleCheck({UserRoleEnum.OWNER})
    public ResponseEntity<?> createFood(
            @PathVariable String storeId,
            @RequestBody FoodRequestDto requestDto) {
        try {
            Store findStore = foodService.getFindStore(storeId);
            FoodResponseDto foodResponseDto = foodService.createFood(findStore, requestDto);
            log.info("foodResponseDto: {}", foodResponseDto);
            return ResponseEntity.ok().body(foodResponseDto);
        } catch(RuntimeException e) {
            // 예외발생시
            log.info("FoodController createFood() 메서드 문제발생, 오류 메시지 - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 가게주인 입장에서 자신이 등록한 모든 음식 목록 조회하기
    // 고객 입장에서 자신이 클릭한 음식점의 모든 음식 목록 조회하기
    @RoleCheck({UserRoleEnum.OWNER, UserRoleEnum.CUSTOMER, UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @GetMapping("/{storeId}/list") // http://localhost:8000/api/food/{storeId}/list
    public ResponseEntity<?> findAll(@PathVariable String storeId) {
        log.info("/api/food/{}/list: GET, ", storeId);

        List<FoodResponseDto> foodList = foodService.findAll(storeId);
        if(!foodList.isEmpty()) { // foodList.size() !== 0
            return ResponseEntity.ok(foodList);
        }
        return ResponseEntity.ok("등록된 음식이 없습니다.");
    }

    // http://localhost:8080/11111111-1111-1111-1111-111111111111/e87dc578-d998-4718-b8a9-1e1325d98eb1
    // 가게 주인이 등록한 특정 메뉴 조회
    @RoleCheck({UserRoleEnum.OWNER, UserRoleEnum.CUSTOMER, UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @GetMapping("/{storeId}/{foodId}")
    public FoodResponseDto findFood(@PathVariable String storeId, @PathVariable String foodId) {
        log.info("/api/food/{}/{}: GET", storeId, foodId);
        return foodService.findFood(storeId, foodId);
    }

    @RoleCheck({UserRoleEnum.OWNER})
    // 가게 주인만 접근 가능한 등록 메뉴 수정
    // 부분 수정만 진행하기 위해 PatchMapping 함.
    @PatchMapping("/{storeId}/{foodId}")
    public ResponseEntity<FoodResponseDto> updateFoodInfo(@PathVariable String storeId,
                                                          @PathVariable String foodId,
                                                          @RequestBody FoodRequestDto updateRequestDto) {

        log.info("/api/food/{}/{}: PUT, 수정할 데이터: {}", storeId, foodId, updateRequestDto);
        FoodResponseDto foodResponseDto = foodService.updateFoodInfo(storeId, foodId, updateRequestDto);
        return ResponseEntity.ok(foodResponseDto);
    }

    // 가게 주인만 접근 가능한 메뉴 삭제 기능
    @RoleCheck({UserRoleEnum.OWNER})
    @PatchMapping("/{storeId}/{foodId}/hide")
    public ResponseEntity<?> hideOneMenu(@PathVariable String storeId, @PathVariable String foodId) {
        log.info("/api/food/{}/{}/hide: 숨김처리기능", storeId, foodId);
        return ResponseEntity.ok(foodService.hideOneMenu(storeId, foodId)); // user 를 받아서 전달한다.
    }
}