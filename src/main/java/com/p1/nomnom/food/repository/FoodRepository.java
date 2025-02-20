package com.p1.nomnom.food.repository;

import com.p1.nomnom.food.entity.Food;
import com.p1.nomnom.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import java.util.List;
import java.util.UUID;

public interface FoodRepository extends JpaRepository<Food, UUID> {

    // Food Entity 와 Store Entity 는 ManyToOne 관계, 단방향 (Food Entity 가 Store 의 pk 를 외래키로 가짐),
    // Store 객체를 전달함 --> Store 객체의 id(pk) 를 얻어 Food Entity 에서 조회함
    List<Food> findAllFoodListByStore(Store store);
    // Optional<Food> findByStoreAndId(Store store, UUID foodId);
}