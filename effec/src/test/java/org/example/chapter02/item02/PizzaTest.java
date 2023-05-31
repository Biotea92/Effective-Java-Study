package org.example.chapter02.item02;

import org.junit.jupiter.api.Test;

import static org.example.chapter02.item02.NyPizza.Size.SMALL;
import static org.example.chapter02.item02.Pizza.Topping.*;
import static org.junit.jupiter.api.Assertions.*;

// 계층적 빌더 사용 (21쪽)
class PizzaTest {

    @Test
    void test() {
        NyPizza pizza = new NyPizza.Builder(SMALL)
                .addTopping(SAUSAGE)
                .addTopping(ONION)
                .build();
        Calzone calzone = new Calzone.Builder()
                .addTopping(HAM)
                .sauceInside()
                .build();

        System.out.println(pizza);
        System.out.println(calzone);
    }
}