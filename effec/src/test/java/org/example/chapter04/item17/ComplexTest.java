package org.example.chapter04.item17;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComplexTest {

    @Test
    void test() {
        Integer num1 = Integer.valueOf(127);
        Integer num2 = Integer.valueOf(127);

        // -128 ~ 127 까지는 캐시를 사용한다.
        assertTrue(num1 == num2);

        Integer num3 = Integer.valueOf(128);
        Integer num4 = Integer.valueOf(128);

        // 128 부터는 캐시를 사용하지 않는다.
        assertFalse(num3 == num4);

        // 캐시를 사용하지 않는 경우에는 equals 로 비교해야 한다.
        assertTrue(num3.equals(num4));
    }
}