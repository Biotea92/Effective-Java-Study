package org.example.chapter08.item49;

import java.math.BigInteger;

public class Example {

    /**
     * 현재 값 mod m을 반환한다. 이 메서드는 항상 음이 아닌 BigInteger를 반환한다.
     *
     * @param m 계수 (양수여야한다.)
     * @return 현재 값 mod m
     * @throws ArithmeticException if the modulus value is less than or equal to zero
     */
    public BigInteger mode(BigInteger m) {
        if (m.signum() <= 0)
            throw new ArithmeticException("Modulus <= 0: " + m);
        // ... 계산수행

        return BigInteger.ZERO;
    }

    public static void main(String[] args) {
//        this.strategy = Objects.requireNonNull(strategy, "전략");
//        Objects.checkIndex(index, list.size());
//        Objects.checkFromToIndex(fromIndex, toIndex, list.size());
//        Objects.checkFromIndexSize(fromIndex, size, list.size());
    }

    private static void sort(long a[], int offset, int length) {
        assert a != null;
        assert offset >= 0 && offset <= a.length;
        assert length >= 0 && length <= a.length - offset;
        // ... 계산 수행
    }
}
