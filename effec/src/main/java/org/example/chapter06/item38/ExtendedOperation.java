package org.example.chapter06.item38;

import java.util.function.BiFunction;

public enum ExtendedOperation implements Operation {
    EXP("^", (x, y) -> Math.pow(x, y)),
    REMAINDER("%", (x, y) -> x % y);

    private final String symbol;
    private final BiFunction<Double, Double, Double> function;

    ExtendedOperation(String symbol, BiFunction<Double, Double, Double> function) {
        this.symbol = symbol;
        this.function = function;
    }

    @Override
    public String symbol() {
        return symbol;
    }

    @Override
    public BiFunction<Double, Double, Double> function() {
        return function;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
