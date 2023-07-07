package org.example.chapter06.item38;

import java.util.function.BiFunction;

// 코드 38-1 인터페이스를 이용해 확장 가능 열거 타입을 흉내 냈다. (232쪽)
public interface Operation {
    String symbol();
    BiFunction<Double, Double, Double> function();

    default double apply(double x, double y) {
        return function().apply(x, y);
    }
}
