package com.p1.nomnom.food.controller;

import com.p1.nomnom.food.dto.request.FoodRequestDto;
import com.p1.nomnom.food.dto.response.FoodResponseDto;
import com.p1.nomnom.food.service.FoodService;
import com.p1.nomnom.store.entity.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController // @Controller + @ResponseBody
@RequestMapping("/api/food")
@RequiredArgsConstructor
@Slf4j(topic="FoodController")
public class FoodController {
    private final FoodService foodService;

    /* User 객체를 받는다.
    - User 객체가 없는 경우 -->                              비회원일 경우
    - User 객체가 있는 경우 -->  User 객체의 Role을 확인 --> Role 이 Master, Manager, Customer 일 경우
    특정가게 Id의 값을 얻어 특정가게 id의 값을 가진 Food 인스턴스들을 모두 가져온다.

    - User 객체가 있는 경우 -->  User 객체의 Role을 확인 --> Owner 일 경우
    자신의 storeId 값에 따라 자신이 등록한 음식 목록들을 보여준다.
     */

    // http://localhost:8080/api/food/{storeId}/create
    // 1. User 객체의 Role이 Owner인 사람만 이 요청을 할 수 있게 하고 싶음
    //    아니면 @AuthenticationPrincipal User user --> user 에서 Role 을 꺼내서 확인한 후 Owner 가 아닌 유저들은
    //    "접근 권한이 없습니다" 라는 메시지를 반환해야 하는 로직으로 작성해야 하는지?
    @PostMapping("/{storeId}/create")
    // 등록을 했으니 클라이언트에(가게주인의 브라우저) 반환하는 것은 등록이 되었다거나 안됐다거나 하는 메시지를 반환
    // 그래서 타입은 String? 아니면 ResponseEntity<String>?
    public ResponseEntity<?> createFood(
            @PathVariable String storeId,
            @RequestBody FoodRequestDto requestDto) {

        try {
            // storeId 로 Store 객체 확인
            Store findStore = foodService.getFindStore(storeId);
            // 메뉴 등록을 성공했을 경우 등록한 메뉴 정보를 반환함
            return ResponseEntity.ok().body(foodService.createFood(findStore, requestDto));
        } catch(RuntimeException e) {
            // 예외발생시
            log.info("FoodController createFood() 메서드 문제발생, 오류 메시지 - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 가게주인 입장에서 자신이 등록한 모든 음식 목록 조회하기
    // 고객 입장에서 자신이 클릭한 음식점의 모든 음식 목록 조회하기
    @GetMapping("/{storeId}/list") // http://localhost:8000/api/food/{storeId}/list
    public ResponseEntity<?> findAll(@PathVariable String storeId) {
        log.info("/api/food/{}/list: GET, ", storeId);

        // 1. Role 이 Owner 인 객체인지 확인
        // 2. storeId 로 Store 객체 얻기
        // 3. 등록한 음식이 있는지 확인
        List<FoodResponseDto> foodList = foodService.findAll(storeId);
        if(!foodList.isEmpty()) { // foodList.size() !== 0
            return ResponseEntity.ok(foodList);
        }
        return ResponseEntity.ok("등록된 음식이 없습니다.");
    }

    // 가게 주인이 등록한 특정 메뉴 조회
    @GetMapping("/{storeId}/{foodId}")
    public FoodResponseDto findFood(@PathVariable String storeId, @PathVariable String foodId) {
        log.info("/api/food/{}/{}: GET", storeId, foodId);
        // 1. User 객체를 받아서 Role 이 Owner 인지 확인
        // 2. storeId 가 존재하는지 확인

        // 3. storeId 를 기준으로 foodId 가 존재하는지 확인하기
        // null 이 올 수도 있다.
        return foodService.findFood(storeId, foodId);
    }

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
    @PatchMapping("/{storeId}/{foodId}/hide")
    public ResponseEntity<?> hideOneMenu(@PathVariable String storeId, @PathVariable String foodId) {
        log.info("/api/food/{}/{}/hide: 숨김처리기능", storeId, foodId);
        return ResponseEntity.ok(foodService.hideOneMenu(storeId, foodId)); // user 를 받아서 전달한다.
    }
}