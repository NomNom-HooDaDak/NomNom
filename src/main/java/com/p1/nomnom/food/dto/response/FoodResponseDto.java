package com.p1.nomnom.food.dto.response;

import com.p1.nomnom.food.entity.Food;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class FoodResponseDto {
    private String name;
    private String description;
    private String price;
    private String image;

    public FoodResponseDto(Food food) {
        // food.
        this.name = food.getName();
        this.description = food.getDescription();
        this.price = food.getPrice();
        this.image = food.getImage();
    }

}