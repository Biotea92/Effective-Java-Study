package org.example.chapter02.item02.lombokbuilder;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;

@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(builderMethodName = "")
public class NutritionFacts {

    private int servingSize; // required
    private int servings; // required
    private int calories; // optional
    private int fat; // optional
    private int sodium; // optional
    private int carbohydrate; // optional

    public static NutritionFactsBuilder builder(int servingSize, int servings) {
        return new NutritionFactsBuilder().servingSize(servingSize).servings(servings);
    }

    public static void main(String[] args) {
        NutritionFacts nutritionFacts = NutritionFacts.builder(240, 8)
                .calories(100)
                .fat(0)
                .sodium(35)
                .carbohydrate(27)
                .build();
    }
}
