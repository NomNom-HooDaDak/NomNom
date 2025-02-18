package com.p1.nomnom.orderItems.service;
import com.p1.nomnom.food.entity.Food;
import com.p1.nomnom.food.service.FoodService;
import com.p1.nomnom.orderItems.dto.OrderItemDto;
import com.p1.nomnom.orderItems.entity.OrderItem;
import com.p1.nomnom.orders.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderItemService {
    private final FoodService foodService;

    public List<OrderItem> createOrderItems(Order order, List<OrderItemDto> orderItemDtos) {
        // ✅ Food ID → Food 객체 맵핑 (한 번의 DB 조회)
        Map<UUID, Food> foodMap = foodService.getFoodsByIds(
                orderItemDtos.stream().map(OrderItemDto::getFoodId).toList() // 바로 Food 조회
        ).stream().collect(Collectors.toMap(Food::getId, food -> food));

        // ✅ Food ID → Quantity 매핑
        Map<UUID, Integer> quantityMap = orderItemDtos.stream()
                .collect(Collectors.toMap(OrderItemDto::getFoodId, OrderItemDto::getQuantity));

        // ✅ OrderItem 생성 (Food ID 기준으로 매핑)
        return foodMap.values().stream()
                .map(food -> OrderItem.create(
                        order,
                        food.getId(),
                        food.getName(),
                        quantityMap.getOrDefault(food.getId(), 1),
                        food.getPrice()
                ))
                .toList();
    }
}