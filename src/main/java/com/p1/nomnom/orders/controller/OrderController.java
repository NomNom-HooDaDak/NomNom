package com.p1.nomnom.orders.controller;

import com.p1.nomnom.orders.dto.request.OrderRequestDto;
import com.p1.nomnom.orders.dto.response.OrderListResponseDto;
import com.p1.nomnom.orders.dto.response.OrderResponseDto;
import com.p1.nomnom.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public OrderResponseDto createOrder(
            @RequestParam Long userId,
            @RequestBody OrderRequestDto orderRequestDto
    ) {
        return orderService.createOrder(userId, orderRequestDto);
    }
}
