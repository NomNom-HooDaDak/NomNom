package com.p1.nomnom.orderItems.service;
import com.p1.nomnom.food.entity.Food;
import com.p1.nomnom.food.service.FoodService;
import com.p1.nomnom.orderItems.dto.OrderItemDto;
import com.p1.nomnom.orderItems.entity.OrderItem;
import com.p1.nomnom.orders.entity.Order;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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

    @PersistenceContext
    private final EntityManager entityManager;

    public List<OrderItem> createOrderItems(Order order, List<OrderItemDto> orderItemDtos) {
        // ✅ Food ID → Food 객체 맵핑 (한 번의 DB 조회)
        Map<UUID, Food> foodMap = foodService.getFoodsByIds(
                orderItemDtos.stream().map(OrderItemDto::getFoodId).toList() // 바로 Food 조회
        ).stream().collect(Collectors.toMap(Food::getId, food -> food));

        // ✅ Food ID → Quantity 매핑
        Map<UUID, Integer> quantityMap = orderItemDtos.stream()
                .collect(Collectors.toMap(OrderItemDto::getFoodId, OrderItemDto::getQuantity));

        // ✅ OrderItem 생성 (Food ID 기준으로 매핑)
        List<OrderItem> orderItems = foodMap.values().stream()
                .map(food -> OrderItem.create(
                        order,
                        food.getId(),
                        food.getName(),
                        quantityMap.getOrDefault(food.getId(), 1),
                        food.getPrice()
                ))
                .toList();

        // ✅ 저장할 아이템 개수에 따라 로직 분기
        if (orderItems.size() < 5) {
            // ⚡ 소량의 데이터 → 단일 insert
            orderItems.forEach(entityManager::persist);
        } else {
            // ⚡ 대량의 데이터 → Batch Insert
            int batchSize = 10;
            for (int i = 0; i < orderItems.size(); i++) {
                entityManager.persist(orderItems.get(i));

                if (i % batchSize == 0 && i > 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }
            entityManager.flush();
            entityManager.clear();
        }

        return orderItems;
    }
}