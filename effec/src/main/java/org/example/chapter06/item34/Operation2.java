package org.example.chapter06.item34;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public enum Operation2 {

    PLUS("+", (x, y) -> x + y),
    MINUS("-", (x, y) -> x - y),
    TIMES("*", (x, y) -> x * y),
    DIVIDE("/", (x, y) -> x / y);

    private final String symbol;
    private final BiFunction<Double, Double, Double> function;

    Operation2(String symbol, BiFunction<Double, Double, Double> function) {
        this.symbol = symbol;
        this.function = function;
    }

    public String symbol() {
        return symbol;
    }

    public double apply(double x, double y) {
        return function.apply(x, y);
    }

    @Override
    public String toString() {
        return symbol;
    }

    private static final Map<String, Operation2> stringToEnum = Stream.of(values()).collect(toMap(Object::toString, e -> e));

    public static Operation2 fromString(String symbol) {
        return stringToEnum.get(symbol);
    }

    public static void main(String[] args) {
        double x = Double.parseDouble("2");
        double y = Double.parseDouble("4");
        for (Operation2 op : Operation2.values())
            System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
    }
}
