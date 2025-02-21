package com.p1.nomnom.food.controller;

import com.p1.nomnom.ai.service.AiService;
import com.p1.nomnom.food.dto.request.FoodRequestDto;
import com.p1.nomnom.food.dto.response.FoodResponseDto;
import com.p1.nomnom.food.service.FoodService;
import com.p1.nomnom.security.aop.CurrentUser;
import com.p1.nomnom.security.aop.CurrentUserInject;
import com.p1.nomnom.security.aop.RoleCheck;
import com.p1.nomnom.security.aop.UserContext;
import com.p1.nomnom.user.entity.UserRoleEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController // @Controller + @ResponseBody
@RequestMapping("/api/food")
@RequiredArgsConstructor
@Slf4j(topic="FoodController")
public class FoodController {
    private final FoodService foodService;
    private final AiService aiService;

    @Operation(summary = "음식 등록", description = "가게 주인이 새로운 음식을 등록합니다.")
    @ApiResponse(responseCode = "200", description = "음식이 성공적으로 등록되었습니다.")
    @CurrentUserInject
    @PostMapping("/{storeId}/create")
    @RoleCheck({UserRoleEnum.OWNER})
    public ResponseEntity<?> createFood(
            @CurrentUser UserContext userContext,
            @PathVariable UUID storeId,
            @RequestBody FoodRequestDto requestDto) {
        try {
            FoodResponseDto foodResponseDto = foodService.createFood(storeId, requestDto, userContext);
            log.info("foodResponseDto: {}", foodResponseDto);
            return ResponseEntity.ok().body(foodResponseDto);
        } catch(RuntimeException e) {
            // 예외발생시
            e.printStackTrace();
            log.info("FoodController createFood() 메서드 문제발생, 오류 메시지 - {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @Operation(summary = "모든 음식 목록 조회", description = "특정 가게의 모든 음식을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "특정 가게의 음식 목록 조회 성공")
    @RoleCheck({UserRoleEnum.OWNER, UserRoleEnum.CUSTOMER, UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @GetMapping("/{storeId}/list") // http://localhost:8000/api/food/{storeId}/list
    public ResponseEntity<?> findAll(@PathVariable UUID storeId) {
        log.info("/api/food/{}/list: GET, ", storeId);

        List<FoodResponseDto> foodList = foodService.findAll(storeId);
        if(!foodList.isEmpty()) { // foodList.size() !== 0
            return ResponseEntity.ok(foodList);
        }
        return ResponseEntity.ok("등록된 음식이 없습니다.");
    }

    @Operation(summary = "음식 상세 조회", description = "특정 가게의 특정 음식 정보를 상세히 볼 수 있습니다")
    @ApiResponse(responseCode = "200", description = "(단일) 음식 조회 성공")
    @RoleCheck({UserRoleEnum.OWNER, UserRoleEnum.CUSTOMER, UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @GetMapping("/{storeId}/{foodId}")
    public FoodResponseDto findFood(@PathVariable UUID storeId, @PathVariable UUID foodId) {
        log.info("/api/food/{}/{}: GET", storeId, foodId);
        return foodService.findFood(storeId, foodId);
    }

    @Operation(summary = "음식 정보 수정", description = "가게 주인인 특정 음식 정보를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "음식 정보 수정 성공")
    @RoleCheck({UserRoleEnum.OWNER})
    @PatchMapping("/{storeId}/{foodId}")
    public ResponseEntity<FoodResponseDto> updateFoodInfo(@PathVariable UUID storeId,
                                                          @PathVariable UUID foodId,
                                                          @RequestBody FoodRequestDto updateRequestDto) {

        log.info("/api/food/{}/{}: PUT, 수정할 데이터: {}", storeId, foodId, updateRequestDto);
        FoodResponseDto foodResponseDto = foodService.updateFoodInfo(storeId, foodId, updateRequestDto);
        return ResponseEntity.ok(foodResponseDto);
    }


    @Operation(summary = "음식 숨김 처리", description = "가게 주인이 등록한 특정 음식 정보를 목록에서 숨깁니다.")
    @ApiResponse(responseCode = "200", description = "음식 숨김 처리 성공")
    @RoleCheck({UserRoleEnum.OWNER})
    @PatchMapping("/{storeId}/{foodId}/hide")
    public ResponseEntity<?> hideOneMenu(@PathVariable UUID storeId, @PathVariable UUID foodId) {
        log.info("/api/food/{}/{}/hide: 숨김처리기능", storeId, foodId);
        return ResponseEntity.ok(foodService.hideOneMenu(storeId, foodId)); // user 를 받아서 전달한다.
    }
}