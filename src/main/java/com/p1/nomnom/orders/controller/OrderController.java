package com.p1.nomnom.orders.controller;

import com.p1.nomnom.orders.dto.request.OrderSearchRequestDto;
import com.p1.nomnom.orders.dto.response.OrderListResponseDto;
import com.p1.nomnom.security.aop.CurrentUser;
import com.p1.nomnom.security.aop.CurrentUserInject;
import com.p1.nomnom.security.aop.UserContext;
import com.p1.nomnom.orders.dto.request.OrderRequestDto;
import com.p1.nomnom.orders.dto.response.OrderResponseDto;
import com.p1.nomnom.orders.service.OrderService;
import com.p1.nomnom.security.aop.RoleCheck;
import com.p1.nomnom.user.entity.UserRoleEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "주문 API", description = "주문 API")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @CurrentUserInject
    @Operation(summary = "주문 등록", description = "주문을 등록합니다.")
    @ApiResponse(responseCode = "200", description = "주문 등록 성공")
    @RoleCheck({UserRoleEnum.CUSTOMER})
    public OrderResponseDto createOrder(
            @CurrentUser @Parameter(hidden = true) UserContext userContext,
            @RequestBody OrderRequestDto orderRequestDto
    ) {
        return orderService.createOrder(userContext, orderRequestDto);
    }

    @Operation(summary = "특정 주문 조회", description = "특정 주문을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "주문 조회 성공")
    @RoleCheck({UserRoleEnum.CUSTOMER, UserRoleEnum.OWNER, UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @GetMapping("/{orderId}")
    @CurrentUserInject
    public OrderResponseDto getOrder(
            @Parameter(description = "주문서 ID", required = true)
            @PathVariable UUID orderId,
            @CurrentUser @Parameter(hidden = true) UserContext userContext
    ) {
        return orderService.getOrder(orderId, userContext);
    }


    @Operation(summary = "주문 리스트 조회", description = "주문 리스트를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "주문 조회 성공")
    @RoleCheck({UserRoleEnum.CUSTOMER, UserRoleEnum.OWNER, UserRoleEnum.MANAGER, UserRoleEnum.MASTER})
    @GetMapping
    @CurrentUserInject
    public OrderListResponseDto getOrders(
            @CurrentUser @Parameter(hidden = true) UserContext userContext,
            @ModelAttribute OrderSearchRequestDto searchRequest
    ) {
        return orderService.getOrders(userContext, searchRequest);
    }

    @PatchMapping("/{orderId}/cancel")
    @CurrentUserInject
    @Operation(summary = "주문 취소", description = "주문을 취소합니다.")
    @ApiResponse(responseCode = "200", description = "주문 취소 성공")
    @RoleCheck({UserRoleEnum.CUSTOMER, UserRoleEnum.OWNER, UserRoleEnum.MASTER, UserRoleEnum.MANAGER})
    public ResponseEntity<Void> cancelOrder(
            @PathVariable UUID orderId,
            @CurrentUser @Parameter(hidden = true) UserContext userContext
    ) {
        orderService.cancelOrder(orderId, userContext);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{orderId}/delete")
    @CurrentUserInject
    @Operation(summary = "주문 삭제", description = "주문을 숨김 처리(soft delete)합니다.")
    @ApiResponse(responseCode = "200", description = "주문 삭제 성공")
    @RoleCheck({UserRoleEnum.CUSTOMER, UserRoleEnum.OWNER, UserRoleEnum.MASTER, UserRoleEnum.MANAGER})
    public ResponseEntity<Void> deleteOrder(
            @PathVariable UUID orderId,
            @CurrentUser @Parameter(hidden = true) UserContext userContext
    ) {
        orderService.deleteOrder(orderId, userContext);
        return ResponseEntity.ok().build();
    }
}
