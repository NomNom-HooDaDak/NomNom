package com.p1.nomnom.food.service;

import com.p1.nomnom.food.entity.Food;
import com.p1.nomnom.food.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FoodService {
    private final FoodRepository foodRepository;

    public List<Food> getFoodsByIds(List<UUID> foodIds) {
        return foodRepository.findAllById(foodIds);
    }
}
